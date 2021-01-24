package cc.darhao.lifecalc.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
* @Auther 鲁智深
* @Date 2021/1/18 21:44
*/
@Configuration
public class Log4j2DataSourceHolder {

    private static DruidDataSourceAutoConfigure configure;


    @Autowired
    public void setDruidDataSourceAutoConfigure(DruidDataSourceAutoConfigure configure){
        Log4j2DataSourceHolder.configure = configure;
    }


    public static Connection getConnection() throws SQLException {
        return Log4j2DataSourceHolder.configure.dataSource().getConnection();
    }

}
