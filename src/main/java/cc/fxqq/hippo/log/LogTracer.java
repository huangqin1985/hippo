package cc.fxqq.hippo.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/* 类名：LogTracer
* 用途：日志跟踪器，利用spring的aop打印方法调用的开始时间和结束时间，以及耗时。
*/
@Aspect        //标注增强处理类（切面类）
@Component     //交由Spring容器管理
@Slf4j
@Order(1)      //设置优先级，值越低优先级越高
public class LogTracer {

   /**
    * 定义一个切入点.
    *
    * ~ 第一个 * 代表任意修饰符及任意返回值.
    * ~ 第二个 * 任意包名
    * ~ 第三个 * 代表任意方法.
    * ~ 第四个 * 定义在web包或者子包
    * ~ 第五个 * 任意方法
    * ~ .. 匹配任意数量的参数.
    */
//   @Pointcut("execution(public void cc.fxqq.hippo.task.ReportTask.update*(..))")
//   public void invokeLog(){}
//   
//   @Pointcut("execution(public void cc.fxqq.hippo.service.TradeOrderService.update*(..))")
//   public void invokeLog2(){}

   /**
    * 方法：在被调用方法的前后打印时间及耗时
    * @param proceedingJoinPoint 处理连接点
    * @return 被调用方法的处理结果
    * @throws Throwable 异常
    */
//   @Around("invokeLog()")
//   public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//       //打印当前时间
//       long startTime = System.currentTimeMillis();
//
//       //pjp.proceed()方法个人理解为是一个对业务方法的模拟，可是在这个方法前后插入想做的事情。
//       Object result = proceedingJoinPoint.proceed();
//
//       long finishTime = System.currentTimeMillis();
//       log.info(proceedingJoinPoint.toString() + "耗时：" + (finishTime-startTime) + "ms");
//
//       return result;
//   }
//   
//   @Around("invokeLog2()")
//   public Object doAround2(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//       //打印当前时间
//       long startTime = System.currentTimeMillis();
//
//       //pjp.proceed()方法个人理解为是一个对业务方法的模拟，可是在这个方法前后插入想做的事情。
//       Object result = proceedingJoinPoint.proceed();
//
//       long finishTime = System.currentTimeMillis();
//       log.info(proceedingJoinPoint.toString() + "耗时：" + (finishTime-startTime) + "ms");
//
//       return result;
//   }
}
