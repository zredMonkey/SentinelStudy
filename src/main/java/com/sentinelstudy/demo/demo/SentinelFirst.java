package com.sentinelstudy.demo.demo;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 *   主要以流量为切入点，从限流、流量整形、熔断降级、系统负载保护、热点防护等多个维度来帮助开发者保障微服务的稳定性
 *   JVM参数：-Dcsp.sentinel.dashboard.server=localhost:8080
 *
 *   熔断降级设计理念：
 *   通过并发线程数进行限制
 *       和资源池隔离的方法不同，Sentinel 通过限制资源并发线程的数量，来减少不稳定资源对其它资源的影响。
 *       这样不但没有线程切换的损耗，也不需要您预先分配线程池的大小。当某个资源出现不稳定的情况下，例如响应时间变长，
 *       对资源的直接影响就是会造成线程数的逐步堆积。当线程数在特定资源上堆积到一定的数量之后，对该资源的新请求就会被拒绝。
 *       堆积的线程完成任务后才开始继续接收请求。
 *
 *   通过响应时间对资源进行降级
 *       除了对并发线程数进行控制以外，Sentinel 还可以通过响应时间来快速降级不稳定的资源。当依赖的资源出现响应时间过长后，
 *       所有对该资源的访问都会被直接拒绝，直到过了指定的时间窗口之后才重新恢复。
 *
 *   系统自适应保护
 *     Sentinel 同时提供系统维度的自适应保护能力。防止雪崩，是系统防护中重要的一环。当系统负载较高的时候，如果还持续让请求进入，可能会导致系统崩溃，
 *     无法响应。在集群环境下，网络负载均衡会把本应这台机器承载的流量转发到其它的机器上去。如果这个时候其它的机器也处在一个边缘状态的时候，
 *     这个增加的流量就会导致这台机器也崩溃，最后导致整个集群不可用。
 *     针对这个情况，Sentinel 提供了对应的保护机制，让系统的入口流量和系统的负载达到一个平衡，保证系统在能力范围之内处理最多的请求。
 *
 *   Sentinel 的主要工作机制如下：
 *      对主流框架提供适配或者显示的 API，来定义需要保护的资源，并提供设施对资源进行实时统计和调用链路分析。
 *      根据预设的规则，结合对资源的实时统计信息，对流量进行控制。同时，Sentinel 提供开放的接口，方便您定义及改变规则。
 *      Sentinel 提供实时的监控系统，方便您快速了解目前系统的状态。
 * @Date: 2020/9/13 15:54
 **/
public class SentinelFirst {

    public static void main(String[] args) {
        // 配置规则.
        initFlowRules();
        while (true) {
            // 名为HelloWorld的资源是否触发保护规则，如果抛出异常则触发了保护规则
            try (Entry entry = SphU.entry("HelloWorld")) {
                // 被保护的逻辑
                System.out.println("hello world sentinel~");
            } catch (BlockException ex) {
                // 处理被流控的逻辑
                System.out.println("触发保护规则-----------blocked!");
            }
            // 睡眠
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 通过流控规则来指定允许该资源通过的请求次数，
    // 例如下面的代码定义了资源 HelloWorld 每秒最多只能通过 20 个请求
    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("HelloWorld");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setControlBehavior(0);
        GatewayParamFlowItem gatewayParamFlowItem = new GatewayParamFlowItem();
        gatewayParamFlowItem.setParseStrategy(0);
        rule.setCount(23);
        rules.add(rule);
        // 加载规则
        FlowRuleManager.loadRules(rules);
    }


    // 注解支持模块，来定义我们的资源.这样，helloWorld() 方法就成了我们的一个资源。
    @SentinelResource("HelloWorld")
    public void helloWorld() {
        // 资源中的逻辑
        System.out.println("hello world");
    }
}
