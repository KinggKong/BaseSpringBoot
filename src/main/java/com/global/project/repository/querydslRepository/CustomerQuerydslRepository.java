package com.global.project.repository.querydslRepository;

import com.global.project.dto.CustomerDto;
import com.global.project.entity.Customer;
import com.global.project.entity.QCustomer;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.global.project.entity.QCustomer.customer;

@Repository
public class CustomerQuerydslRepository extends QuerydslRepositorySupport {
    @Autowired
    EntityManager entityManager;
    @Autowired
    JPAQueryFactory queryFactory;

    public CustomerQuerydslRepository() {
        super(Customer.class);
    }

    public Page<CustomerDto> findByPage(Pageable page, String textSearch){
        QCustomer qCustomer = customer;
//        JPAQuery<CustomerDto> query = new JPAQuery<>(entityManager);
//        query.from(qCustomer)
//                .offset(page.getOffset())
//                .limit(page.getPageSize());
//        List<CustomerDto> customers = query.fetch();
        QueryResults<CustomerDto>query = queryFactory
                .select(Projections.fields(CustomerDto.class,
                        qCustomer.id,
                        qCustomer.age,
                        qCustomer.name.as("nameDto")
                        ))
                .from(qCustomer)
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetchResults();
        long count = query.getTotal();
        Page<CustomerDto> pageResult = new PageImpl<>(query.getResults(), page, count);
        return pageResult;
    }
}
