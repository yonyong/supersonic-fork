package com.tencent.supersonic.headless.query.parser.calcite.sql.node;


import com.tencent.supersonic.headless.query.parser.calcite.Configuration;
import com.tencent.supersonic.headless.query.parser.calcite.s2sql.Constants;
import com.tencent.supersonic.headless.query.parser.calcite.schema.SemanticSqlDialect;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.sql.JoinType;
import org.apache.calcite.sql.SqlAsOperator;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlWith;
import org.apache.calcite.sql.SqlWriterConfig;
import org.apache.calcite.sql.fun.SqlStdOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.commons.lang3.StringUtils;

public abstract class SemanticNode {

    public static Set<SqlKind> AGGREGATION_KIND = new HashSet<>();
    public static Set<String> AGGREGATION_FUNC = new HashSet<>();

    static {
        AGGREGATION_KIND.add(SqlKind.AVG);
        AGGREGATION_KIND.add(SqlKind.COUNT);
        AGGREGATION_KIND.add(SqlKind.SUM);
        AGGREGATION_KIND.add(SqlKind.MAX);
        AGGREGATION_KIND.add(SqlKind.MIN);
        AGGREGATION_KIND.add(SqlKind.OTHER_FUNCTION); //  more
        AGGREGATION_FUNC.add("sum");
        AGGREGATION_FUNC.add("count");
        AGGREGATION_FUNC.add("max");
        AGGREGATION_FUNC.add("avg");
        AGGREGATION_FUNC.add("min");
    }

    public static SqlNode parse(String expression, SqlValidatorScope scope) throws Exception {
        SqlParser sqlParser = SqlParser.create(expression, Configuration.getParserConfig());
        SqlNode sqlNode = sqlParser.parseExpression();
        scope.validateExpr(sqlNode);
        return sqlNode;
    }

    public static SqlNode buildAs(String asName, SqlNode sqlNode) throws Exception {
        SqlAsOperator sqlAsOperator = new SqlAsOperator();
        SqlIdentifier sqlIdentifier = new SqlIdentifier(asName, SqlParserPos.ZERO);
        return new SqlBasicCall(sqlAsOperator, new ArrayList<>(Arrays.asList(sqlNode, sqlIdentifier)),
                SqlParserPos.ZERO);
    }

    public static String getSql(SqlNode sqlNode) {
        SqlWriterConfig config = SqlPrettyWriter.config().withDialect(SemanticSqlDialect.DEFAULT)
                .withKeywordsLowerCase(true).withClauseEndsLine(true).withAlwaysUseParentheses(false)
                .withSelectListItemsOnSeparateLines(false).withUpdateSetListNewline(false).withIndentation(0);

        UnaryOperator<SqlWriterConfig> sqlWriterConfigUnaryOperator = (c) -> config;
        return sqlNode.toSqlString(sqlWriterConfigUnaryOperator).getSql();
    }

    public static boolean isNumeric(String expr) {
        return StringUtils.isNumeric(expr);
    }

    public static List<SqlNode> expand(SqlNode sqlNode, SqlValidatorScope scope) throws Exception {
        if (!isIdentifier(sqlNode)) {
            List<SqlNode> sqlNodeList = new ArrayList<>();
            expand(sqlNode, sqlNodeList);
            return sqlNodeList;
        }
        return new ArrayList<>(Arrays.asList(sqlNode));
    }

    public static void expand(SqlNode sqlNode, List<SqlNode> sqlNodeList) {
        if (sqlNode instanceof SqlIdentifier) {
            sqlNodeList.add(sqlNode);
            return;
        }
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            for (SqlNode sqlNo : sqlBasicCall.getOperandList()) {
                expand(sqlNo, sqlNodeList);
            }
        }
    }

    public static boolean isIdentifier(SqlNode sqlNode) {
        return sqlNode instanceof SqlIdentifier;
    }

    public static SqlNode getAlias(SqlNode sqlNode, SqlValidatorScope scope) throws Exception {
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            if (sqlBasicCall.getKind().equals(SqlKind.AS) && sqlBasicCall.getOperandList().size() > 1) {
                return sqlBasicCall.getOperandList().get(1);
            }
        }
        if (sqlNode instanceof SqlIdentifier) {
            return sqlNode;
        }
        return null;
    }

    public static Set<String> getSelect(SqlNode sqlNode) {
        SqlNode table = getTable(sqlNode);
        if (table instanceof SqlSelect) {
            SqlSelect tableSelect = (SqlSelect) table;
            return tableSelect.getSelectList().stream()
                    .map(s -> (s instanceof SqlIdentifier) ? ((SqlIdentifier) s).names.get(0)
                            : (((s instanceof SqlBasicCall) && s.getKind().equals(SqlKind.AS))
                                    ? ((SqlBasicCall) s).getOperandList().get(1).toString() : ""))
                    .collect(Collectors.toSet());
        }
        return new HashSet<>();
    }

    public static SqlNode getTable(SqlNode sqlNode) {
        if (sqlNode instanceof SqlBasicCall) {
            SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
            if (sqlBasicCall.getOperator().getKind().equals(SqlKind.AS)) {
                if (sqlBasicCall.getOperandList().get(0) instanceof SqlSelect) {
                    SqlSelect table = (SqlSelect) sqlBasicCall.getOperandList().get(0);
                    return table;
                }
            }
        }
        return sqlNode;
    }

    private static void sqlVisit(SqlNode sqlNode, Map<String, String> parseInfo) {
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case SELECT:
                queryVisit(sqlNode, parseInfo);
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall) sqlNode;
                sqlVisit(sqlBasicCall.getOperandList().get(0), parseInfo);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin) sqlNode;
                sqlVisit(sqlJoin.getLeft(), parseInfo);
                sqlVisit(sqlJoin.getRight(), parseInfo);
                break;
            case UNION:
                ((SqlBasicCall) sqlNode).getOperandList().forEach(node -> {
                    sqlVisit(node, parseInfo);
                });
                break;
            case WITH:
                SqlWith sqlWith = (SqlWith) sqlNode;
                sqlVisit(sqlWith.body, parseInfo);
                break;
            default:
                break;
        }
    }

    private static void queryVisit(SqlNode select, Map<String, String> parseInfo) {
        if (select == null) {
            return;
        }
        SqlSelect sqlSelect = (SqlSelect) select;
        SqlNodeList selectList = sqlSelect.getSelectList();
        selectList.getList().forEach(list -> {
            fieldVisit(list, parseInfo, "");
        });
        fromVisit(sqlSelect.getFrom(), parseInfo);
    }

    private static void fieldVisit(SqlNode field, Map<String, String> parseInfo, String func) {
        if (field == null) {
            return;
        }
        SqlKind kind = field.getKind();
        //System.out.println(kind);
        // aggfunction
        if (AGGREGATION_KIND.contains(kind)) {
            SqlOperator sqlCall = ((SqlCall) field).getOperator();
            if (AGGREGATION_FUNC.contains(sqlCall.toString().toLowerCase())) {
                List<SqlNode> operandList = ((SqlBasicCall) field).getOperandList();
                for (int i = 0; i < operandList.size(); i++) {
                    fieldVisit(operandList.get(i), parseInfo, sqlCall.toString().toUpperCase());
                }
                return;
            }
        }
        if (kind.equals(SqlKind.IDENTIFIER)) {
            addTagField(field.toString(), parseInfo, func);
            return;
        }
        if (kind.equals(SqlKind.AS)) {
            List<SqlNode> operandList1 = ((SqlBasicCall) field).getOperandList();
            SqlNode left = operandList1.get(0);
            fieldVisit(left, parseInfo, "");
            return;
        }
        if (field instanceof SqlBasicCall) {
            List<SqlNode> operandList = ((SqlBasicCall) field).getOperandList();
            for (int i = 0; i < operandList.size(); i++) {
                fieldVisit(operandList.get(i), parseInfo, "");
            }
        }
        if (field instanceof SqlNodeList) {
            ((SqlNodeList) field).getList().forEach(node -> {
                fieldVisit(node, parseInfo, "");
            });
        }
    }

    private static void addTagField(String exp, Map<String, String> parseInfo, String func) {
        Set<String> fields = new HashSet<>();
        for (String f : exp.split("[^\\w]+")) {
            if (Pattern.matches("(?i)[a-z\\d_]+", f)) {
                fields.add(f);
            }
        }
        if (!fields.isEmpty()) {
            parseInfo.put(Constants.SQL_PARSER_FIELD, fields.stream().collect(Collectors.joining(",")));
        }
    }

    private static void fromVisit(SqlNode from, Map<String, String> parseInfo) {
        SqlKind kind = from.getKind();
        switch (kind) {
            case IDENTIFIER:
                SqlIdentifier sqlIdentifier = (SqlIdentifier) from;
                addTableName(sqlIdentifier.toString(), parseInfo);
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall) from;
                SqlNode selectNode1 = sqlBasicCall.getOperandList().get(0);
                if (!SqlKind.UNION.equals(selectNode1.getKind())) {
                    if (!SqlKind.SELECT.equals(selectNode1.getKind())) {
                        addTableName(selectNode1.toString(), parseInfo);
                    }
                }
                sqlVisit(selectNode1, parseInfo);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin) from;
                sqlVisit(sqlJoin.getLeft(), parseInfo);
                sqlVisit(sqlJoin.getRight(), parseInfo);
                break;
            case SELECT:
                sqlVisit(from, parseInfo);
                break;
            default:
                break;
        }
    }

    private static void addTableName(String exp, Map<String, String> parseInfo) {
        if (exp.indexOf(" ") > 0) {
            return;
        }
        if (exp.indexOf("_") > 0) {
            if (exp.split("_").length > 1) {
                String[] dbTb = exp.split("\\.");
                if (Objects.nonNull(dbTb) && dbTb.length > 0) {
                    parseInfo.put(Constants.SQL_PARSER_TABLE, dbTb.length > 1 ? dbTb[1] : dbTb[0]);
                    parseInfo.put(Constants.SQL_PARSER_DB, dbTb.length > 1 ? dbTb[0] : "");
                }
            }
        }
    }

    public static Map<String, String> getDbTable(SqlNode sqlNode) {
        Map<String, String> parseInfo = new HashMap<>();
        sqlVisit(sqlNode, parseInfo);
        return parseInfo;
    }

    public static RelNode getRelNode(CalciteSchema rootSchema, SqlToRelConverter sqlToRelConverter, String sql)
            throws SqlParseException {
        SqlValidator sqlValidator = Configuration.getSqlValidator(rootSchema);
        return sqlToRelConverter.convertQuery(
                sqlValidator.validate(SqlParser.create(sql, SqlParser.Config.DEFAULT).parseStmt()), false, true).rel;
    }

    public static SqlBinaryOperator getBinaryOperator(String val) {
        if (val.equals("=")) {
            return SqlStdOperatorTable.EQUALS;
        }
        if (val.equals(">")) {
            return SqlStdOperatorTable.GREATER_THAN;
        }
        if (val.equals(">=")) {
            return SqlStdOperatorTable.GREATER_THAN_OR_EQUAL;
        }
        if (val.equals("<")) {
            return SqlStdOperatorTable.LESS_THAN;
        }
        if (val.equals("<=")) {
            return SqlStdOperatorTable.LESS_THAN_OR_EQUAL;
        }
        if (val.equals("!=")) {
            return SqlStdOperatorTable.NOT_EQUALS;
        }
        return SqlStdOperatorTable.EQUALS;
    }

    public static SqlLiteral getJoinSqlLiteral(String joinType) {
        if (Objects.nonNull(joinType) && !joinType.isEmpty()) {
            if (joinType.toLowerCase().contains(JoinType.INNER.lowerName)) {
                return SqlLiteral.createSymbol(JoinType.INNER, SqlParserPos.ZERO);
            }
            if (joinType.toLowerCase().contains(JoinType.LEFT.lowerName)) {
                return SqlLiteral.createSymbol(JoinType.LEFT, SqlParserPos.ZERO);
            }
            if (joinType.toLowerCase().contains(JoinType.RIGHT.lowerName)) {
                return SqlLiteral.createSymbol(JoinType.RIGHT, SqlParserPos.ZERO);
            }
            if (joinType.toLowerCase().contains(JoinType.FULL.lowerName)) {
                return SqlLiteral.createSymbol(JoinType.FULL, SqlParserPos.ZERO);
            }
        }
        return SqlLiteral.createSymbol(JoinType.INNER, SqlParserPos.ZERO);
    }

}
