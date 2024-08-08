package it.univaq.lucaepio.webmarket.util;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Questa classe è responsabile dell'inizializzazione e della configurazione di FreeMarker per l'intera applicazione web. 
 * Assicura che FreeMarker sia correttamente configurato e disponibile per tutte le servlet che necessitano di renderizzare template.
 * 
 * @author lucat
 */
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