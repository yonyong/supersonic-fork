package com.tencent.supersonic.headless.model.application;

import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.headless.api.model.request.DatabaseReq;
import com.tencent.supersonic.headless.api.model.response.DatabaseResp;
import com.tencent.supersonic.headless.api.model.response.ModelResp;
import com.tencent.supersonic.headless.api.model.response.QueryResultWithSchemaResp;
import com.tencent.supersonic.headless.model.domain.DatabaseService;
import com.tencent.supersonic.headless.model.domain.ModelService;
import com.tencent.supersonic.headless.model.domain.adaptor.engineadapter.EngineAdaptor;
import com.tencent.supersonic.headless.model.domain.adaptor.engineadapter.EngineAdaptorFactory;
import com.tencent.supersonic.headless.model.domain.dataobject.DatabaseDO;
import com.tencent.supersonic.headless.model.domain.pojo.ModelFilter;
import com.tencent.supersonic.headless.model.domain.utils.DatabaseConverter;
import com.tencent.supersonic.headless.model.domain.pojo.Database;
import com.tencent.supersonic.headless.model.domain.repository.DatabaseRepository;
import com.tencent.supersonic.headless.model.domain.utils.JdbcDataSourceUtils;
import com.tencent.supersonic.headless.model.domain.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DatabaseServiceImpl implements DatabaseService {

    private final SqlUtils sqlUtils;
    private DatabaseRepository databaseRepository;
    private ModelService datasourceService;

    public DatabaseServiceImpl(DatabaseRepository databaseRepository,
                               SqlUtils sqlUtils,
                               @Lazy ModelService datasourceService) {
        this.databaseRepository = databaseRepository;
        this.sqlUtils = sqlUtils;
        this.datasourceService = datasourceService;
    }

    @Override
    public boolean testConnect(DatabaseReq databaseReq, User user) {
        Database database = DatabaseConverter.convert(databaseReq);
        return JdbcDataSourceUtils.testDatabase(database);
    }

    @Override
    public DatabaseResp createOrUpdateDatabase(DatabaseReq databaseReq, User user) {
        Database database = DatabaseConverter.convert(databaseReq);
        DatabaseDO databaseDO = getDatabaseDO(databaseReq.getId());
        if (databaseDO != null) {
            database.updatedBy(user.getName());
            DatabaseConverter.convert(database, databaseDO);
            databaseRepository.updateDatabase(databaseDO);
            return DatabaseConverter.convert(databaseDO);
        }
        database.createdBy(user.getName());
        databaseDO = DatabaseConverter.convert(database);
        databaseRepository.createDatabase(databaseDO);
        return DatabaseConverter.convert(databaseDO);
    }

    @Override
    public List<DatabaseResp> getDatabaseList(User user) {
        List<DatabaseResp> databaseResps =
                databaseRepository.getDatabaseList()
                .stream().map(DatabaseConverter::convert)
                .collect(Collectors.toList());
        fillPermission(databaseResps, user);
        return databaseResps;
    }

    private void fillPermission(List<DatabaseResp> databaseResps, User user) {
        databaseResps.forEach(databaseResp -> {
            if (databaseResp.getAdmins().contains(user.getName())
                    || user.getName().equalsIgnoreCase(databaseResp.getCreatedBy())
                    || user.isSuperAdmin()) {
                databaseResp.setHasPermission(true);
                databaseResp.setHasEditPermission(true);
                databaseResp.setHasUsePermission(true);
            }
            if (databaseResp.getViewers().contains(user.getName())) {
                databaseResp.setHasUsePermission(true);
            }
        });
    }

    @Override
    public void deleteDatabase(Long databaseId) {
        ModelFilter modelFilter = new ModelFilter();
        modelFilter.setDatabaseId(databaseId);
        List<ModelResp> modelResps = datasourceService.getModelList(modelFilter);
        if (!CollectionUtils.isEmpty(modelResps)) {
            List<String> datasourceNames = modelResps.stream()
                    .map(ModelResp::getName).collect(Collectors.toList());
            String message = String.format("该数据库被模型%s使用，无法删除", datasourceNames);
            throw new RuntimeException(message);
        }
        databaseRepository.deleteDatabase(databaseId);
    }

    @Override
    public DatabaseResp getDatabase(Long id) {
        DatabaseDO databaseDO = databaseRepository.getDatabase(id);
        return DatabaseConverter.convert(databaseDO);
    }

    @Override
    public QueryResultWithSchemaResp executeSql(String sql, Long id, User user) {
        DatabaseResp databaseResp = getDatabase(id);
        if (databaseResp == null) {
            return new QueryResultWithSchemaResp();
        }
        List<String> admins = databaseResp.getAdmins();
        List<String> viewers = databaseResp.getViewers();
        if (!admins.contains(user.getName())
                && !viewers.contains(user.getName())
                && !databaseResp.getCreatedBy().equalsIgnoreCase(user.getName())
                && !user.isSuperAdmin()) {
            String message = String.format("您暂无当前数据库%s权限, 请联系数据库管理员%s开通",
                    databaseResp.getName(),
                    String.join(",", admins));
            throw new RuntimeException(message);
        }
        return executeSql(sql, databaseResp);
    }

    @Override
    public QueryResultWithSchemaResp executeSql(String sql, DatabaseResp databaseResp) {
        return queryWithColumns(sql, databaseResp);
    }

    private QueryResultWithSchemaResp queryWithColumns(String sql, DatabaseResp databaseResp) {
        QueryResultWithSchemaResp queryResultWithColumns = new QueryResultWithSchemaResp();
        SqlUtils sqlUtils = this.sqlUtils.init(databaseResp);
        log.info("query SQL: {}", sql);
        sqlUtils.queryInternal(sql, queryResultWithColumns);
        return queryResultWithColumns;
    }

    private DatabaseDO getDatabaseDO(Long id) {
        return databaseRepository.getDatabase(id);
    }

    @Override
    public QueryResultWithSchemaResp getDbNames(Long id) {
        DatabaseResp databaseResp = getDatabase(id);
        EngineAdaptor engineAdaptor = EngineAdaptorFactory.getEngineAdaptor(databaseResp.getType());
        String metaQueryTpl = engineAdaptor.getDbMetaQueryTpl();
        return queryWithColumns(metaQueryTpl, databaseResp);
    }

    @Override
    public QueryResultWithSchemaResp getTables(Long id, String db) {
        DatabaseResp databaseResp = getDatabase(id);
        EngineAdaptor engineAdaptor = EngineAdaptorFactory.getEngineAdaptor(databaseResp.getType());
        String metaQueryTpl = engineAdaptor.getTableMetaQueryTpl();
        String metaQuerySql = String.format(metaQueryTpl, db);
        return queryWithColumns(metaQuerySql, databaseResp);
    }

    @Override
    public QueryResultWithSchemaResp getColumns(Long id, String db, String table) {
        DatabaseResp databaseResp = getDatabase(id);
        EngineAdaptor engineAdaptor = EngineAdaptorFactory.getEngineAdaptor(databaseResp.getType());
        String metaQueryTpl = engineAdaptor.getColumnMetaQueryTpl();
        String metaQuerySql = String.format(metaQueryTpl, db, table);
        return queryWithColumns(metaQuerySql, databaseResp);
    }

}
