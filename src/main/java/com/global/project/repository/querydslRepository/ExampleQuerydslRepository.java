package com.global.project.repository.querydslRepository;

import com.global.project.dto.ExampleDto;
import com.global.project.entity.Example;
import com.global.project.entity.QExample;
import com.global.project.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.global.project.entity.QExample.example;
import static com.global.project.entity.QUser.user;

@Repository
public class ExampleQuerydslRepository extends QuerydslRepositorySupport {
    @Autowired
    JPAQueryFactory queryFactory;

    public ExampleQuerydslRepository() {
        super(Example.class);
    }

    public Page<ExampleDto> findByPage(Pageable page, String textSearch){
        QExample qExample = example;
        QUser qUser = user;
        BooleanBuilder builder =  new BooleanBuilder();
        if(textSearch != null && !textSearch.isEmpty()){
            builder.and(qExample.name.like("%" + textSearch + "%"));
            builder.or(qExample.age.like("%" + textSearch + "%"));
        }
        QueryResults<ExampleDto>query = queryFactory
                .select(Projections.fields(ExampleDto.class,
                        qExample.id,
                        qExample.age,
                        qExample.name.as("nameDto"),
                        ExpressionUtils.as(
                                JPAExpressions.select(qUser.username)
                                        .from(qUser)
                                        .where(qUser.username.eq("admin"))
                                , "username")
                        ))
                .from(qExample)
                .where(builder)
                .orderBy(qExample.id.desc(), qExample.name.desc(), qExample.age.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetchResults();
        long count = query.getTotal();
        Page<ExampleDto> pageResult = new PageImpl<>(query.getResults(), page, count);
        return pageResult;
    }
}
