package plus.myj.nfb.builder;

import plus.myj.nfb.entity.Novel;

/**
 * 构建器接口
 */
public interface Builder {
    /**
     * 开始构建
     *
     * @param novel novel对象，包括所有需要的信息
     * @return 自身支持的电子书格式的文件的二进制串
     * @throws Exception 异常
     */
    byte[] build(Novel novel) throws Exception;
}
