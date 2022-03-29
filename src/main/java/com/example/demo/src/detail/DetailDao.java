package com.example.demo.src.detail;

import com.example.demo.src.detail.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class DetailDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    public GetDetailRes getDetail(long contentIdx) {
        String getDetailQuery = "select id, title, ageRate, date_format(productionDate, '%Y') as productionYear, " +
                "summary, type, mainImgUrl, previewUrl, runningTime, percentage, logoImgUrl from Content where id = ?";
        long getDetailParams = contentIdx;
        return this.jdbcTemplate.queryForObject(getDetailQuery,
                (rs, rowNum) -> new GetDetailRes(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getLong("ageRate"),
                        rs.getString("productionYear"),
                        rs.getString("summary"),
                        rs.getString("type"),
                        rs.getString("mainImgUrl"),
                        rs.getString("previewUrl"),
                        rs.getString("runningTime"),
                        rs.getString("percentage"),
                        rs.getString("logoImgUrl")),
                getDetailParams);
    }
}
