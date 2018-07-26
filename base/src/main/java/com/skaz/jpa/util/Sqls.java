package com.skaz.jpa.util;


import com.google.common.collect.Lists;
import com.skaz.utils.Strings;

import java.util.List;

public class Sqls {

    private static final String[] COUNT_KEY_WORD = {" group ", " order ", " having ", " limit ", " procedure ", " for ", " lock "};
    private static final String TABLE_ALIAS = "table_alias";

    public static String buildSql(String sql) {
//        if (!sql.startsWith(Constants.MYCAT_DBTYPE_SLAVE_SQL)) {
//            return Constants.MYCAT_DBTYPE_SLAVE_SQL + " " + sql;
//        }
        return sql;
    }

    /**
     * 构建统计语句
     */
    public static String buildCountSql(String sql) {
        String upperCaseSql = sql.toLowerCase();
        StringBuilder countSql = new StringBuilder();
        // sql 统计查询前缀
        String prefix = "select count(1) ";
        // select 关键字
        String selectKey = "select";
        // from 关键字
        String fromKey = "from";
        // 带有group by 的进行特殊处理，直接返回
        if (Strings.isContainsIgnoreCase(sql, "group by")) {
            return prefix + fromKey + "(" + sql + ")" + TABLE_ALIAS;
        }
        // 主sql中 from 关键字的位置
        Integer fromRelIndex = upperCaseSql.indexOf(fromKey);
        List<Integer> fromIndexList = getStrKeyIndexList(upperCaseSql, fromKey);
        if (fromIndexList.size() > 1) {
            // 子查询处理
            for (int i = 0; i < fromIndexList.size(); i++) {
                Integer selectKeyCount = getStrKeyIndexList(upperCaseSql.substring(0, fromIndexList.get(i)), selectKey).size() - 1;
                if (selectKeyCount == i) {
                    fromRelIndex = fromIndexList.get(i);
                    break;
                }
            }
            fromRelIndex -= fromKey.length();
        }
        // 主sql from 关键字真实位置之后的sql语句
        String afterFromSql = sql.substring(fromRelIndex);
        if (Strings.isContains(upperCaseSql, ")")) {
            // 查找最后一个反括号；跳过子查询中的 order by 语句
            upperCaseSql = Strings.substringAfterLast(upperCaseSql, ")");
        }
        if (Strings.isContainsIgnoreCase(upperCaseSql, "order")) {
            String query = Strings.substringAfterAndContainsIgnoreCases(upperCaseSql, " order by ");
            // 删除主sql 中的 order by 之后的语句；结果为 from 到 order by 之前的语句
            afterFromSql = afterFromSql.substring(0, afterFromSql.length() - query.length());
        }
        // 拼装sql
        sql = countSql.append(prefix + afterFromSql).toString();
        return buildSql(sql);
    }

    public static String buildPageSql(String sql, int pageNo, int pageSize) {
        return buildPageSql(sql, pageNo, pageSize, true);
    }

    public static String buildPageSql(String sql, int pageNo, int pageSize, boolean hasCount) {
        StringBuilder pageSql = new StringBuilder();
        // hasCount=false时,因使用的expectedSize+1,构建offset则需要expectedSize-1
        int offset = hasCount ? pageNo * pageSize : pageNo * (pageSize - 1);
        pageSql.append(sql);
        // 查询时需要使用expectedSize,以多查询一条数据
        pageSql.append(" limit ").append(offset).append(", ").append(pageSize);
        return pageSql.toString();
    }

    /**
     * 获取指定字符串在整个字符串中的位置集合
     *
     * @param str 整个字符转
     * @param key 要查找的额字符串
     */
    private static List<Integer> getStrKeyIndexList(String str, String key) {
        int temp = 0;
        List<Integer> indexList = Lists.newArrayList();
        while ((temp = str.indexOf(key, temp)) != -1) {
            temp = temp + key.length();
            indexList.add(temp);
        }
        return indexList;
    }

    public static String substringCountBeforeKeyWord(String str) {
        return Strings.substringBeforeIgnoreCases(str, COUNT_KEY_WORD);
    }

    public static String getLikeField(String field) {
        return "%" + field + "%";
    }

}
