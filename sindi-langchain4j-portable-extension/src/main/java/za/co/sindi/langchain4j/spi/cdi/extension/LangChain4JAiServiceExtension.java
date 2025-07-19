package za.co.sindi.langchain4j.spi.cdi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;
import jakarta.enterprise.inject.spi.WithAnnotations;
import za.co.sindi.commons.utils.Annotations;
import za.co.sindi.commons.utils.Reflections;
import za.co.sindi.langchain4j.spi.cdi.AiService;

/**
 * @author Buhake Sindi
 * @since 11 August 2024
 */
public class LangChain4JAiServiceExtension implements Extension {

	private static final Logger LOGGER = Logger.getLogger(LangChain4JAiServiceExtension.class.getName());
	private static final Set<Class<?>> detectedAIServicesDeclaredInterfaces = new HashSet<>();

    public <T> void processAnnotatedType(@Observes @WithAnnotations(AiService.class) ProcessAnnotatedType<T> pat) {
    	LOGGER.info("Scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
    	if (pat.getAnnotatedType().getJavaClass().isInterface()) {
            LOGGER.info("processAnnotatedType register " + pat.getAnnotatedType().getJavaClass().getName());
            detectedAIServicesDeclaredInterfaces.add(pat.getAnnotatedType().getJavaClass());
        } else {
            LOGGER.warning("processAnnotatedType reject " + pat.getAnnotatedType().getJavaClass().getName()
                    + " which is not an interface");
            pat.veto();
        }
     }
	
	public void processInjectionPoints(@Observes ProcessInjectionPoint<?, ?> event) {
        if (event.getInjectionPoint().getBean() == null) {
        	Class<?> rawType = Reflections.getRawType(event.getInjectionPoint().getType());
        	if (classSatisfies(rawType, AiService.class))
        		detectedAIServicesDeclaredInterfaces.add(rawType);
        }
        
        if (Instance.class.equals(Reflections.getRawType(event.getInjectionPoint().getType()))) {
        	Class<?> parameterizedType = Reflections.getRawType(getFacadeType(event.getInjectionPoint()));
        	if (classSatisfies(parameterizedType, AiService.class))
        		detectedAIServicesDeclaredInterfaces.add(parameterizedType);
        }
    }
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
		LOGGER.info("Finished the scanning process.");
		
		for (Class<?> aiServiceClass : detectedAIServicesDeclaredInterfaces) {
            LOGGER.info("afterBeanDiscovery create synthetic :  " + aiServiceClass.getName());
            afterBeanDiscovery.addBean(new LangChain4JAIServiceBean<>(aiServiceClass, beanManager));
        }
	}
	
	private <T extends Annotation> boolean classSatisfies(Class<?> clazz, Class<T> annotationClass) {
		if (!clazz.isInterface()) return false;
		T annotation = Annotations.findAnnotation(clazz, annotationClass);
		return (annotation != null);
	}
	
	private Type getFacadeType(InjectionPoint injectionPoint) {
        Type genericType = injectionPoint.getType();
        if (genericType instanceof ParameterizedType) {
            return ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return null;
    }
}
