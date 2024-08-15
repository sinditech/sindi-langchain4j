package za.co.sindi.langchain4j.spi;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import dev.langchain4j.service.sindi.AiService;
import jakarta.annotation.Priority;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.ClassConfig;
import jakarta.enterprise.inject.build.compatible.spi.Enhancement;
import jakarta.enterprise.inject.build.compatible.spi.FieldConfig;
import jakarta.enterprise.inject.build.compatible.spi.Synthesis;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanBuilder;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.lang.model.AnnotationInfo;
import jakarta.enterprise.lang.model.declarations.ClassInfo;
import jakarta.enterprise.lang.model.declarations.FieldInfo;
import jakarta.enterprise.lang.model.types.ClassType;
import za.co.sindi.commons.utils.Classes;
import za.co.sindi.commons.utils.Reflections;
import za.co.sindi.commons.utils.Strings;

/**
 * @author Buhake Sindi
 * @since 08 August 2024
 */
public class LangChainAiServiceBuildCompatibleExtension implements BuildCompatibleExtension {

	private static final Logger LOGGER = Logger.getLogger(LangChainAiServiceBuildCompatibleExtension.class.getName());
	private static final Set<Class<?>> detectedAIServicesDeclaredInterfaces = new HashSet<>();
//    private static final Set<String> detectedTools = new HashSet<>();
    public static final String PARAM_INTERFACE_CLASS = "interfaceClass";

    @Enhancement(types = Object.class, withSubtypes = true)
    @Priority(10)
    public void executeClassConfigEnhancement(ClassConfig classConfig) throws ClassNotFoundException {
//    	LOGGER.info("*** Execute ClassConfig Enhancement ***");
        ClassInfo classInfo = classConfig.info();
        detectAiService(classInfo);
    }

	@Enhancement(types = Object.class, /*withAnnotations=AiService.class,*/ withSubtypes = true)
	@Priority(20)
	public void executeFieldConfigEnhancement(FieldConfig config) throws ClassNotFoundException {
//		LOGGER.info("*** Execute FieldConfig Enhancement ***");
		FieldInfo info = config.info();
//		LOGGER.info("Analyze FieldConfig Enhancement " + info.type().isType() + " - " + info.type().isClass());
		if (info.type().isClass()) {
			ClassType classType = info.type().asClass();
			ClassInfo classInfo = classType.declaration();
			detectAiService(classInfo);
		}
	}
	
	private void detectAiService(ClassInfo classInfo) throws ClassNotFoundException {
		if (classInfo.isInterface()) {
//			LOGGER.info("Analyze Enhancement " + classInfo.name());
			AnnotationInfo annotationInfo = classInfo.annotation(AiService.class);
			if (annotationInfo != null) {
				LOGGER.info("Analyze Enhancement " + classInfo.name() + " with AnnotationInfo " + annotationInfo.name());
				Class<?> rawType = Reflections.getRawType(Classes.getClass(classInfo.name(), false));
				detectedAIServicesDeclaredInterfaces.add(rawType);
//				AiService aiServiceAnnotation = Annotations.findAnnotation(rawType, AiService.class);
//				detectedTools.addAll(Arrays.stream(aiServiceAnnotation.tools()).map(Class::getName).collect(Collectors.toSet()));
//	            LOGGER.info("Current detected tools : " + detectedTools);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Synthesis
    public void synthesisAllRegisterAIServices(SyntheticComponents syntheticComponents) throws ClassNotFoundException {
//        LOGGER.info("synthesisAllRegisterAIServices");
        for (Class<?> interfaceClass : detectedAIServicesDeclaredInterfaces) {
        	LOGGER.info("Create synthetic " + interfaceClass);
            AiService annotation = interfaceClass.getAnnotation(AiService.class);
//            detectedTools.addAll(Arrays.stream(annotation.tools()).map(Class::getName).collect(Collectors.toSet()));
            ((SyntheticBeanBuilder<Object>)syntheticComponents.addBean(interfaceClass))
           					  .createWith(AIServiceCreator.class)
			                  .type(interfaceClass)
			                  .scope(annotation.scope())
			                  .name(Strings.uncapitalize(interfaceClass.getName()) + "Proxy")
			                  .withParam(PARAM_INTERFACE_CLASS, interfaceClass);
        }
    }
}
