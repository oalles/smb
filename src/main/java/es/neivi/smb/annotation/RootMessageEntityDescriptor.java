package es.neivi.smb.annotation;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import es.neivi.smb.exceptions.UniqueRootMessageEntityRequired;


/**
 * Some useful methods regarding {@link RootEventEntity} annotations.
 */
public class RootMessageEntityDescriptor {

	private Class<?> rootMessageEntityType;

	/**
	 * Checks that there is one and only one event type annotated as
	 * {@link RootEventEntity} to set the event hierarchy model.
	 * 
	 * @return the type annotated as root for all the events.
	 * @throws ClassNotFoundException
	 *             whether there is no domain object marked as
	 *             {@link RootEventEntity} in the classpath.
	 * @throws UniqueRootMessageEntityRequired
	 *             whether multiple domain objects are marked as
	 *             {@link RootEventEntity} when there has to be just one.
	 */
	@PostConstruct
	public void checkRootEventEntityType() throws ClassNotFoundException {

		// Exists
		// Class.forName(rootEventEntityType.getName(), false, classLoader);

		// Package bPackage = getClass().getPackage();
		// String basePackage = (bPackage == null) ? null : bPackage.getName();
		String basePackage = "es.neivi";

		Set<Class<?>> candidates = new HashSet<Class<?>>();
		if (StringUtils.hasText(basePackage)) {

			ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
					false);

			// componentProvider
			// .setResourceLoader(new PathMatchingResourcePatternResolver(
			// Thread.currentThread().getContextClassLoader()));

			// componentProvider.setResourceLoader(resourceLoader);

			componentProvider.addIncludeFilter(new AnnotationTypeFilter(
					RootMessageEntity.class, false, false));

			for (BeanDefinition candidate : componentProvider
					.findCandidateComponents(basePackage)) {
				candidates
						.add(ClassUtils.forName(candidate.getBeanClassName(),
								componentProvider.getResourceLoader()
										.getClassLoader()));
			}
		}

		int size = candidates.size();

		if (size == 0)
			throw new ClassNotFoundException();
		else if (size > 1) {
			throw new UniqueRootMessageEntityRequired();
		}

		// size == 1
		Class<?> candidate = candidates.iterator().next();

		this.rootMessageEntityType = (Class<?>) candidate;
	}

	public Class<?> getRootMessageEntityType() {
		return rootMessageEntityType;
	}

	public void setRootMessageEntityType(Class<?> rootMessageEntityType) {
		this.rootMessageEntityType = rootMessageEntityType;
	}
}
