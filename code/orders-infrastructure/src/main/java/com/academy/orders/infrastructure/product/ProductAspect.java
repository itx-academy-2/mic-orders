package com.academy.orders.infrastructure.product;

import com.academy.orders.domain.account.exception.ConcurrentUpdateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class ProductAspect implements Ordered {
  @Around("execution(* com.academy.orders.domain.product.usecase.UpdateProductUseCase.*(..))")
  public Object handleOptimisticLockingFailureException(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    try {
      return proceedingJoinPoint.proceed();
    } catch (OptimisticLockingFailureException ex) {
      log.debug("Optimistic locking failure in method [{}] with arguments {}. Rewriting to ConcurrentUpdateException. Reason: {}",
          proceedingJoinPoint.getSignature(),
          Arrays.toString(proceedingJoinPoint.getArgs()),
          ex.getMessage());
      throw new ConcurrentUpdateException("This product was being modified by someone else at the same time. Try again!", ex);
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
