package cn.cyyself.WebApp;

import cn.endereye.framework.Loader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class LoadListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Loader.load("cn.cyyself.WebApp");
    }
}
