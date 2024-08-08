package it.univaq.lucaepio.webmarket.util;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class FreemarkerConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setServletContextForTemplateLoading(sce.getServletContext(), "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        sce.getServletContext().setAttribute("freemarker_config", cfg);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Pulizia, se necessaria
    }
}