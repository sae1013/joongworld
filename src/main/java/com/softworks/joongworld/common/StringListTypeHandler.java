package com.softworks.joongworld.common;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

/**
 * PostgreSQL text[] 컬럼을 List<String>으로 매핑할 때 사용.
 */
@MappedTypes(List.class)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter,
      JdbcType jdbcType) throws SQLException {
    if (parameter == null) {
      ps.setArray(i, null);
      return;
    }
    Array array = ps.getConnection().createArrayOf("text", parameter.toArray());
    ps.setArray(i, array);
  }

  @Override
  public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return convert(rs.getArray(columnName));
  }

  @Override
  public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return convert(rs.getArray(columnIndex));
  }

  @Override
  public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return convert(cs.getArray(columnIndex));
  }


  private List<String> convert(Array sqlArray) throws SQLException {
    if (sqlArray == null) {
      return Collections.emptyList();
    }
    try {
      Object array = sqlArray.getArray();
      if (array instanceof String[] strings) {
        return Arrays.asList(strings);
      }
      if (array instanceof Object[] objects) {
        return Arrays.stream(objects).map(Object::toString).toList();
      }
      return Collections.emptyList();
    } finally {
      sqlArray.free();
    }
  }
}
