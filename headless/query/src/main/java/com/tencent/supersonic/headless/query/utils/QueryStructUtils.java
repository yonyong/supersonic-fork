package com.tencent.supersonic.headless.query.utils;

import static com.tencent.supersonic.common.pojo.Constants.DAY;
import static com.tencent.supersonic.common.pojo.Constants.DAY_FORMAT;
import static com.tencent.supersonic.common.pojo.Constants.MONTH;
import static com.tencent.supersonic.common.pojo.Constants.UNDERLINE;
import static com.tencent.supersonic.common.pojo.Constants.WEEK;

import com.google.common.collect.Lists;
import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.common.pojo.Aggregator;
import com.tencent.supersonic.common.pojo.DateConf;
import com.tencent.supersonic.common.pojo.DateConf.DateMode;
import com.tencent.supersonic.common.pojo.ItemDateResp;
import com.tencent.supersonic.common.pojo.enums.TypeEnums;
import com.tencent.supersonic.common.util.DateModeUtils;
import com.tencent.supersonic.common.util.SqlFilterUtils;
import com.tencent.supersonic.common.util.StringUtil;
import com.tencent.supersonic.common.util.jsqlparser.FieldExpression;
import com.tencent.supersonic.common.util.jsqlparser.SqlParserAddHelper;
import com.tencent.supersonic.common.util.jsqlparser.SqlParserRemoveHelper;
import com.tencent.supersonic.common.util.jsqlparser.SqlParserSelectHelper;
import com.tencent.supersonic.headless.api.model.pojo.ItemDateFilter;
import com.tencent.supersonic.headless.api.model.pojo.SchemaItem;
import com.tencent.supersonic.headless.api.model.request.ModelSchemaFilterReq;
import com.tencent.supersonic.headless.api.model.response.DimSchemaResp;
import com.tencent.supersonic.headless.api.model.response.DimensionResp;
import com.tencent.supersonic.headless.api.model.response.MetricResp;
import com.tencent.supersonic.headless.api.model.response.MetricSchemaResp;
import com.tencent.supersonic.headless.api.model.response.ModelSchemaResp;
import com.tencent.supersonic.headless.api.query.request.ParseSqlReq;
import com.tencent.supersonic.headless.api.query.request.QueryS2SQLReq;
import com.tencent.supersonic.headless.api.query.request.QueryStructReq;
import com.tencent.supersonic.headless.model.domain.Catalog;
import com.tencent.supersonic.headless.model.domain.pojo.EngineTypeEnum;
import com.tencent.supersonic.headless.query.service.SchemaService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


@Slf4j
@Component
public class QueryStructUtils {

    public static Set<String> internalTimeCols = new HashSet<>(
            Arrays.asList("dayno", "sys_imp_date", "sys_imp_week", "sys_imp_month"));
    public static Set<String> internalCols;

    static {
        internalCols = new HashSet<>(Arrays.asList("plat_sys_var"));
        internalCols.addAll(internalTimeCols);
    }

    private final DateModeUtils dateModeUtils;
    private final SqlFilterUtils sqlFilterUtils;
    private final Catalog catalog;
    @Value("${internal.metric.cnt.suffix:internal_cnt}")
    private String internalMetricNameSuffix;
    @Value("${metricParser.agg.mysql.lowVersion:5.7}")
    private String mysqlLowVersion;
    @Value("${metricParser.agg.ck.lowVersion:20.4}")
    private String ckLowVersion;
    @Autowired
    private SchemaService schemaService;

    private String variablePrefix = "'${";

    public QueryStructUtils(
            DateModeUtils dateModeUtils,
            SqlFilterUtils sqlFilterUtils, Catalog catalog) {

        this.dateModeUtils = dateModeUtils;
        this.sqlFilterUtils = sqlFilterUtils;
        this.catalog = catalog;
    }

    private List<Long> getDimensionIds(QueryStructReq queryStructCmd) {
        List<Long> dimensionIds = new ArrayList<>();
        List<DimensionResp> dimensions = catalog.getDimensions(queryStructCmd.getModelIds());
        Map<String, List<DimensionResp>> pair = dimensions.stream()
                .collect(Collectors.groupingBy(DimensionResp::getBizName));
        for (String group : queryStructCmd.getGroups()) {
            if (pair.containsKey(group)) {
                dimensionIds.add(pair.get(group).get(0).getId());
            }
        }

        List<String> filtersCols = sqlFilterUtils.getFiltersCol(queryStructCmd.getOriginalFilter());
        for (String col : filtersCols) {
            if (pair.containsKey(col)) {
                dimensionIds.add(pair.get(col).get(0).getId());
            }
        }

        return dimensionIds;
    }

    private List<Long> getMetricIds(QueryStructReq queryStructCmd) {
        List<Long> metricIds = new ArrayList<>();
        List<MetricResp> metrics = catalog.getMetrics(queryStructCmd.getModelIds());
        Map<String, List<MetricResp>> pair = metrics.stream().collect(Collectors.groupingBy(SchemaItem::getBizName));
        for (Aggregator agg : queryStructCmd.getAggregators()) {
            if (pair.containsKey(agg.getColumn())) {
                metricIds.add(pair.get(agg.getColumn()).get(0).getId());
            }
        }
        List<String> filtersCols = sqlFilterUtils.getFiltersCol(queryStructCmd.getOriginalFilter());
        for (String col : filtersCols) {
            if (pair.containsKey(col)) {
                metricIds.add(pair.get(col).get(0).getId());
            }
        }
        return metricIds;
    }

    public String getDateWhereClause(QueryStructReq queryStructCmd) {
        DateConf dateInfo = queryStructCmd.getDateInfo();
        if (Objects.isNull(dateInfo) || Objects.isNull(dateInfo.getDateMode())) {
            return "";
        }
        if (dateInfo.getDateMode().equals(DateMode.RECENT)) {
            if (dateInfo.getUnit() <= 0) {
                return "";
            }
        }

        List<Long> dimensionIds = getDimensionIds(queryStructCmd);
        List<Long> metricIds = getMetricIds(queryStructCmd);

        ItemDateResp dateDate = catalog.getItemDate(
                new ItemDateFilter(dimensionIds, TypeEnums.DIMENSION.getName()),
                new ItemDateFilter(metricIds, TypeEnums.METRIC.getName()));
        if (Objects.isNull(dateDate)
                || Strings.isEmpty(dateDate.getStartDate())
                && Strings.isEmpty(dateDate.getEndDate())) {
            if (dateInfo.getDateMode().equals(DateMode.LIST)) {
                return dateModeUtils.listDateStr(dateDate, dateInfo);
            }
            if (dateInfo.getDateMode().equals(DateMode.BETWEEN)) {
                return dateModeUtils.betweenDateStr(dateDate, dateInfo);
            }
            if (dateModeUtils.hasAvailableDataMode(dateInfo)) {
                return dateModeUtils.hasDataModeStr(dateDate, dateInfo);
            }

            return dateModeUtils.defaultRecentDateInfo(queryStructCmd.getDateInfo());
        }
        log.info("dateDate:{}", dateDate);
        return dateModeUtils.getDateWhereStr(dateInfo, dateDate);
    }

    public String generateWhere(QueryStructReq queryStructCmd) {
        String whereClauseFromFilter = sqlFilterUtils.getWhereClause(queryStructCmd.getOriginalFilter());
        String whereFromDate = getDateWhereClause(queryStructCmd);
        return mergeDateWhereClause(queryStructCmd, whereClauseFromFilter, whereFromDate);
    }

    public String mergeDateWhereClause(QueryStructReq queryStructCmd, String whereClauseFromFilter,
            String whereFromDate) {
        if (Strings.isNotEmpty(whereFromDate) && Strings.isNotEmpty(whereClauseFromFilter)) {
            return String.format("%s AND (%s)", whereFromDate, whereClauseFromFilter);
        } else if (Strings.isEmpty(whereFromDate) && Strings.isNotEmpty(whereClauseFromFilter)) {
            return whereClauseFromFilter;
        } else if (Strings.isNotEmpty(whereFromDate) && Strings.isEmpty(whereClauseFromFilter)) {
            return whereFromDate;
        } else if (Objects.isNull(whereFromDate) && Strings.isEmpty(whereClauseFromFilter)) {
            log.info("the current date information is empty, enter the date initialization logic");
            return dateModeUtils.defaultRecentDateInfo(queryStructCmd.getDateInfo());
        }
        return whereClauseFromFilter;
    }

    public Set<String> getResNameEn(QueryStructReq queryStructCmd) {
        Set<String> resNameEnSet = new HashSet<>();
        queryStructCmd.getAggregators().stream().forEach(agg -> resNameEnSet.add(agg.getColumn()));
        resNameEnSet.addAll(queryStructCmd.getGroups());
        queryStructCmd.getOrders().stream().forEach(order -> resNameEnSet.add(order.getColumn()));
        sqlFilterUtils.getFiltersCol(queryStructCmd.getOriginalFilter()).stream().forEach(col -> resNameEnSet.add(col));
        return resNameEnSet;
    }

    public Set<String> getResName(QueryS2SQLReq queryS2SQLReq) {
        Set<String> resNameSet = SqlParserSelectHelper.getAllFields(queryS2SQLReq.getSql())
                .stream().collect(Collectors.toSet());
        return resNameSet;
    }

    public Set<String> getResNameEnExceptInternalCol(QueryStructReq queryStructCmd) {
        Set<String> resNameEnSet = getResNameEn(queryStructCmd);
        return resNameEnSet.stream().filter(res -> !internalCols.contains(res)).collect(Collectors.toSet());
    }

    public Set<String> getResNameEnExceptInternalCol(QueryS2SQLReq queryS2SQLReq, User user) {
        Set<String> resNameSet = getResName(queryS2SQLReq);
        Set<String> resNameEnSet = new HashSet<>();
        ModelSchemaFilterReq filter = new ModelSchemaFilterReq();
        List<Long> modelIds = Lists.newArrayList(queryS2SQLReq.getModelIds());
        filter.setModelIds(modelIds);
        List<ModelSchemaResp> modelSchemaRespList = schemaService.fetchModelSchema(filter, user);
        if (!CollectionUtils.isEmpty(modelSchemaRespList)) {
            List<MetricSchemaResp> metrics = modelSchemaRespList.get(0).getMetrics();
            List<DimSchemaResp> dimensions = modelSchemaRespList.get(0).getDimensions();
            metrics.stream().forEach(o -> {
                if (resNameSet.contains(o.getName())) {
                    resNameEnSet.add(o.getBizName());
                }
            });
            dimensions.stream().forEach(o -> {
                if (resNameSet.contains(o.getName())) {
                    resNameEnSet.add(o.getBizName());
                }
            });
        }
        return resNameEnSet.stream().filter(res -> !internalCols.contains(res)).collect(Collectors.toSet());
    }

    public Set<String> getFilterResNameEn(QueryStructReq queryStructCmd) {
        Set<String> resNameEnSet = new HashSet<>();
        sqlFilterUtils.getFiltersCol(queryStructCmd.getOriginalFilter()).stream().forEach(col -> resNameEnSet.add(col));
        return resNameEnSet;
    }

    public Set<String> getFilterResNameEnExceptInternalCol(QueryStructReq queryStructCmd) {
        Set<String> resNameEnSet = getFilterResNameEn(queryStructCmd);
        return resNameEnSet.stream().filter(res -> !internalCols.contains(res)).collect(Collectors.toSet());
    }

    public Set<String> getFilterResNameEnExceptInternalCol(QueryS2SQLReq queryS2SQLReq) {
        String sql = queryS2SQLReq.getSql();
        Set<String> resNameEnSet = SqlParserSelectHelper.getWhereFields(sql).stream().collect(Collectors.toSet());
        return resNameEnSet.stream().filter(res -> !internalCols.contains(res)).collect(Collectors.toSet());
    }

    public String generateInternalMetricName(Long modelId, List<String> groups) {
        String internalMetricNamePrefix = "";
        List<DimensionResp> dimensions = catalog.getDimensions(Collections.singletonList(modelId));
        if (!CollectionUtils.isEmpty(dimensions)) {
            internalMetricNamePrefix = dimensions.get(0).getModelBizName();
        }
        return internalMetricNamePrefix + UNDERLINE + internalMetricNameSuffix;
    }

    public boolean isSupportWith(EngineTypeEnum engineTypeEnum, String version) {
        if (engineTypeEnum.equals(EngineTypeEnum.MYSQL) && Objects.nonNull(version) && version.startsWith(
                mysqlLowVersion)) {
            return false;
        }
        if (engineTypeEnum.equals(EngineTypeEnum.CLICKHOUSE) && Objects.nonNull(version)
                && StringUtil.compareVersion(version,
                ckLowVersion) < 0) {
            return false;
        }
        return true;
    }

    public String generateZipperWhere(QueryStructReq queryStructCmd) {
        String whereClauseFromFilter = sqlFilterUtils.getWhereClause(queryStructCmd.getOriginalFilter());
        String whereFromDate = getZipperDateWhereClause(queryStructCmd);
        return mergeDateWhereClause(queryStructCmd, whereClauseFromFilter, whereFromDate);
    }

    public String generateZipperWhere(QueryStructReq queryStructCmd, ParseSqlReq parseSqlReq) {
        if (Objects.nonNull(parseSqlReq.getSql()) && !CollectionUtils.isEmpty(parseSqlReq.getTables())
                && Objects.nonNull(queryStructCmd.getDateInfo())) {
            String sql = SqlParserRemoveHelper.removeWhere(parseSqlReq.getSql(),
                    dateModeUtils.getDateCol());
            parseSqlReq.getTables().stream().forEach(t -> {
                if (Objects.nonNull(t)) {
                    List<String> dimensions = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(t.getDimensions())) {
                        dimensions.addAll(t.getDimensions().stream()
                                .filter(d -> !dateModeUtils.getDateCol().contains(d.toLowerCase())).collect(
                                        Collectors.toList()));
                    }
                    dimensions.add(dateModeUtils.getDateColBegin(queryStructCmd.getDateInfo()));
                    dimensions.add(dateModeUtils.getDateColEnd(queryStructCmd.getDateInfo()));
                    t.setDimensions(dimensions);
                }
            });
            return SqlParserAddHelper.addWhere(sql,
                    SqlParserSelectHelper.getTimeFilter(getTimeRanges(queryStructCmd),
                            dateModeUtils.getDateColBegin(queryStructCmd.getDateInfo()),
                            dateModeUtils.getDateColEnd(queryStructCmd.getDateInfo())));
        }
        return parseSqlReq.getSql();
    }

    public String getZipperDateWhereClause(QueryStructReq queryStructCmd) {
        List<ImmutablePair<String, String>> timeRanges = getTimeRanges(queryStructCmd);
        List<String> wheres = new ArrayList<>();
        if (!CollectionUtils.isEmpty(timeRanges)) {
            for (ImmutablePair<String, String> range : timeRanges) {
                String strWhere = dateModeUtils.getDateWhereStr(queryStructCmd.getDateInfo(), range);
                if (!strWhere.isEmpty()) {
                    wheres.add(strWhere);
                }
            }
            if (!wheres.isEmpty()) {
                return wheres.stream().collect(Collectors.joining(" or ", "(", ")"));
            }
        }
        return "";
    }

    public Triple<String, String, String> getBeginEndTime(QueryStructReq queryStructCmd) {
        if (Objects.isNull(queryStructCmd.getDateInfo())) {
            return Triple.of("", "", "");
        }
        DateConf dateConf = queryStructCmd.getDateInfo();
        String dateInfo = dateModeUtils.getSysDateCol(dateConf);
        if (dateInfo.isEmpty()) {
            return Triple.of("", "", "");
        }
        switch (dateConf.getDateMode()) {
            case AVAILABLE:
            case BETWEEN:
                return Triple.of(dateInfo, dateConf.getStartDate(), dateConf.getEndDate());
            case LIST:
                return Triple.of(dateInfo, Collections.min(dateConf.getDateList()),
                        Collections.max(dateConf.getDateList()));
            case RECENT:
                ItemDateResp dateDate = getItemDateResp(queryStructCmd);
                LocalDate dateMax = LocalDate.now().minusDays(1);
                LocalDate dateMin = dateMax.minusDays(dateConf.getUnit() - 1);
                if (Objects.isNull(dateDate)) {
                    return Triple.of(dateInfo, dateMin.format(DateTimeFormatter.ofPattern(DAY_FORMAT)),
                            dateMax.format(DateTimeFormatter.ofPattern(DAY_FORMAT)));
                }
                switch (dateConf.getPeriod()) {
                    case DAY:
                        ImmutablePair<String, String> dayInfo = dateModeUtils.recentDay(dateDate, dateConf);
                        return Triple.of(dateInfo, dayInfo.left, dayInfo.right);
                    case WEEK:
                        ImmutablePair<String, String> weekInfo = dateModeUtils.recentWeek(dateDate, dateConf);
                        return Triple.of(dateInfo, weekInfo.left, weekInfo.right);
                    case MONTH:
                        List<ImmutablePair<String, String>> rets = dateModeUtils.recentMonth(dateDate, dateConf);
                        Optional<String> minBegins = rets.stream().map(i -> i.left).sorted().findFirst();
                        Optional<String> maxBegins = rets.stream().map(i -> i.right).sorted(Comparator.reverseOrder())
                                .findFirst();
                        if (minBegins.isPresent() && maxBegins.isPresent()) {
                            return Triple.of(dateInfo, minBegins.get(), maxBegins.get());
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;

        }
        return Triple.of("", "", "");
    }

    public List<ImmutablePair<String, String>> getTimeRanges(QueryStructReq queryStructCmd) {
        List<ImmutablePair<String, String>> ret = new ArrayList<>();
        if (Objects.isNull(queryStructCmd) || Objects.isNull(queryStructCmd.getDateInfo())) {
            return ret;
        }
        DateConf dateConf = queryStructCmd.getDateInfo();
        switch (dateConf.getDateMode()) {
            case AVAILABLE:
            case BETWEEN:
                ret.add(ImmutablePair.of(dateConf.getStartDate(), dateConf.getEndDate()));
                break;
            case LIST:
                for (String date : dateConf.getDateList()) {
                    ret.add(ImmutablePair.of(date, date));
                }
                break;
            case RECENT:
                ItemDateResp dateDate = getItemDateResp(queryStructCmd);
                LocalDate dateMax = LocalDate.now().minusDays(1);
                LocalDate dateMin = dateMax.minusDays(dateConf.getUnit() - 1);
                if (Objects.isNull(dateDate)) {
                    ret.add(ImmutablePair.of(dateMin.format(DateTimeFormatter.ofPattern(DAY_FORMAT)),
                            dateMax.format(DateTimeFormatter.ofPattern(DAY_FORMAT))));
                    break;
                }
                switch (dateConf.getPeriod()) {
                    case DAY:
                        ret.add(dateModeUtils.recentDay(dateDate, dateConf));
                        break;
                    case WEEK:
                        ret.add(dateModeUtils.recentWeek(dateDate, dateConf));
                        break;
                    case MONTH:
                        List<ImmutablePair<String, String>> rets = dateModeUtils.recentMonth(dateDate, dateConf);
                        ret.addAll(rets);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return ret;
    }

    private ItemDateResp getItemDateResp(QueryStructReq queryStructCmd) {
        List<Long> dimensionIds = getDimensionIds(queryStructCmd);
        List<Long> metricIds = getMetricIds(queryStructCmd);
        ItemDateResp dateDate = catalog.getItemDate(
                new ItemDateFilter(dimensionIds, TypeEnums.DIMENSION.getName()),
                new ItemDateFilter(metricIds, TypeEnums.METRIC.getName()));
        return dateDate;
    }

    public DateConf getDateConfBySql(String sql) {
        List<FieldExpression> fieldExpressions = SqlParserSelectHelper.getFilterExpression(sql);
        if (!CollectionUtils.isEmpty(fieldExpressions)) {
            Set<String> dateList = new HashSet<>();
            String startDate = "";
            String endDate = "";
            String period = "";
            for (FieldExpression f : fieldExpressions) {
                if (Objects.isNull(f.getFieldName()) || !internalCols.contains(f.getFieldName().toLowerCase())) {
                    continue;
                }
                if (Objects.isNull(f.getFieldValue()) || !dateModeUtils.isDateStr(f.getFieldValue().toString())) {
                    continue;
                }
                period = dateModeUtils.getPeriodByCol(f.getFieldName().toLowerCase());
                if ("".equals(period)) {
                    continue;
                }
                if ("=".equals(f.getOperator())) {
                    dateList.add(f.getFieldValue().toString());
                } else if ("<".equals(f.getOperator()) || "<=".equals(f.getOperator())) {
                    if (startDate.isEmpty() || startDate.compareTo(f.getFieldValue().toString()) > 0) {
                        startDate = f.getFieldValue().toString();
                    }
                } else if (">".equals(f.getOperator()) || ">=".equals(f.getOperator())) {
                    if (endDate.isEmpty() || endDate.compareTo(f.getFieldValue().toString()) < 0) {
                        endDate = f.getFieldValue().toString();
                    }
                }
            }
            if (!"".equals(period)) {
                DateConf dateConf = new DateConf();
                dateConf.setPeriod(period);
                if (!CollectionUtils.isEmpty(dateList)) {
                    dateConf.setDateList(new ArrayList<>(dateList));
                    dateConf.setDateMode(DateMode.LIST);
                    return dateConf;
                }
                if (!"".equals(startDate) && !"".equals(endDate)) {
                    dateConf.setStartDate(startDate);
                    dateConf.setEndDate(endDate);
                    dateConf.setDateMode(DateMode.BETWEEN);
                    return dateConf;
                }
            }
        }
        return null;
    }

    public List<String> getDateCol() {
        return dateModeUtils.getDateCol();
    }

    public String getVariablePrefix() {
        return variablePrefix;
    }

}

