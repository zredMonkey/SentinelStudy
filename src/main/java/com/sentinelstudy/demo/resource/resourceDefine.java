package com.sentinelstudy.demo.resource;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * 抛出异常的方式定义资源
 * @Description:
 * @Date: 2020/9/13 21:33
 **/
public class resourceDefine {

    /**
    * @Description:
    *      特别地，若 entry 的时候传入了热点参数，那么 exit 的时候也一定要带上对应的参数（exit(count, args)），否则可能会有统计错误。
    *      这个时候不能使用 try-with-resources 的方式。另外通过 Tracer.trace(ex) 来统计异常信息时，
    *      由于 try-with-resources 语法中 catch 调用顺序的问题，会导致无法正确统计异常数，因此统计异常信息时也不能在 try-with-resources 的
    *      catch 块中调用 Tracer.trace(ex)。
    *
    *
    * @Date: 2020/9/13 21:36
    * @Param:
    * @Return:
    **/
    public void test() {
        // 定义资源
        // 1.5.0 版本开始可以利用 try-with-resources 特性（使用有限制）
        // 资源名可使用任意有业务语义的字符串，比如方法名、接口名或其它可唯一标识的字符串。
        try (Entry entry = SphU.entry("resourceName")) {

            // 被保护的业务逻辑
            // do something here...

        } catch (BlockException ex) {
            // 资源访问阻止，被限流或被降级
            // 在此处进行相应的处理操作
        }
    }

    // 手动 exit 示例：
    public void test1() {
        Entry entry = null;
        // 务必保证 finally 会被执行
        try {
            // 资源名可使用任意有业务语义的字符串，注意数目不能太多（超过 1K），超出几千请作为参数传入而不要直接作为资源名
            // EntryType 代表流量类型（inbound/outbound），其中系统规则只对 IN 类型的埋点生效
            entry = SphU.entry("自定义资源名");
            // 被保护的业务逻辑
            // do something...
        } catch (BlockException ex) {
            // 资源访问阻止，被限流或被降级
            // 进行相应的处理操作
        } catch (Exception ex) {
            // 若需要配置降级规则，需要通过这种方式记录业务异常
            Tracer.traceEntry(ex, entry);
        } finally {
            // 务必保证 exit，务必保证每个 entry 与 exit 配对
            if (entry != null) {
                entry.exit();
            }
        }
    }


    // 热点参数埋点示例：
    public void test2() {
        Entry entry = null;
        String resourceName = "resourceName";
        Object paramA = "paramA";
        Object paramB = "paramB";
        try {
            // 若需要配置例外项，则传入的参数只支持基本类型。
            // EntryType 代表流量类型，其中系统规则只对 IN 类型的埋点生效
            // count 大多数情况都填 1，代表统计为一次调用。
            // 	资源调用的流量类型，是入口流量（EntryType.IN）还是出口流量（EntryType.OUT），注意系统规则只对 IN 生效 默认EntryType.OUT
            // count	int	本次资源调用请求的 token 数目	1
            // args	Object[]	传入的参数，用于热点参数限流	无
            entry = SphU.entry(resourceName, EntryType.IN, 1, paramA, paramB);
            // Your logic here.
        } catch (BlockException ex) {
            // Handle request rejection.
        } finally {
            // 注意：exit 的时候也一定要带上对应的参数，否则可能会有统计错误。
            if (entry != null) {
                entry.exit(1, paramA, paramB);
            }
        }
    }
}
