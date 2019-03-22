package com.tiza.plugin.listener;

import com.tiza.plugin.model.facade.IDataProcess;
import com.tiza.plugin.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: SpringInitializer
 * Author: DIYILIU
 * Update: 2018-12-06 10:49
 */

@Slf4j
public class SpringInitializer implements ApplicationListener {

    private List<Class> protocols;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info("协议解析初始化 ...");

        for (Class protocol : protocols) {
            Map parses = SpringUtil.getBeansOfType(protocol);

            for (Iterator iterator = parses.keySet().iterator(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                IDataProcess process = (IDataProcess) parses.get(key);
                process.init();
            }
        }
    }

    public void setProtocols(List<Class> protocols) {
        this.protocols = protocols;
    }
}
