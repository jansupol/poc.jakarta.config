/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package poc.jakarta.config.internal.loader.weld;

import jakarta.config.Configuration;
import jakarta.config.Loader;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.ProcessInjectionTarget;
import jakarta.enterprise.util.AnnotationLiteral;
import poc.jakarta.config.PocConfigLoader;
import poc.jakarta.config.internal.loader.ReflectUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CdiConfigExtension implements Extension {
    private final PocConfigLoader loader;
    private static final Class<? extends Annotation> CONFIG_ANNOTATION = Configuration.class;
    private final Map<Class<?>, ConfigBean> beanMap = new HashMap<>();

    public CdiConfigExtension() {
        loader = (PocConfigLoader) Loader.bootstrap();
    }

    <T> void observeInjectionTarget(@Observes ProcessInjectionTarget<T> pit) {
        InjectionTarget target = pit.getInjectionTarget();
        List<ConfigBean> configBeans = new LinkedList<>();
        for (InjectionPoint ip : (Iterable<? extends InjectionPoint>) target.getInjectionPoints()) {
            Class<?> ipClass;
            if (ParameterizedType.class.isInstance(ip.getType())) {
                ipClass = ReflectUtil.getClass(ReflectUtil.getType(ip.getType()));
            } else {
                ipClass = (Class<?>) ip.getType();
            }
            Configuration mapping = (Configuration) ipClass.getDeclaredAnnotation(CONFIG_ANNOTATION);
            if (mapping != null && !beanMap.containsKey(ipClass)) {
                ConfigBean bean = new ConfigBean(ipClass, ip.getMember().getName(), mapping.path());
                configBeans.add(bean);
            }
        }
        if (!configBeans.isEmpty()) {
            pit.setInjectionTarget(new ConfigInjectionTarget(target, configBeans));
            for (ConfigBean bean : configBeans) {
                beanMap.put(bean.getBeanClass(), bean);
            }
        }
    }

    void afterDiscoveryObserver(@Observes final AfterBeanDiscovery abd) {
        for (Bean bean : beanMap.values()) {
            abd.addBean(bean);
        }
    }

    private class ConfigInjectionTarget implements InjectionTarget {
        private final InjectionTarget delegate;
        private final List<ConfigBean> beans;

        public ConfigInjectionTarget(InjectionTarget delegate, List<ConfigBean> beans) {
            this.delegate = delegate;
            this.beans = beans;
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
            return delegate.getInjectionPoints();
        }

        @Override
        public void inject(final Object t, final CreationalContext cc) {
            Class<?> objectClass = t.getClass();
            Field field;
            for (ConfigBean bean : beans) {
                try {
                    field = objectClass.getDeclaredField(bean.getName());
                    ReflectUtil.ensureAccessible(field, t);
                    field.set(t, bean.create(cc));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            delegate.inject(t, cc); // here the injection manager is used in HK2Bean
        }

        @Override
        public void postConstruct(final Object t) {
            delegate.postConstruct(t);
        }

        @Override
        public void preDestroy(final Object t) {
            delegate.preDestroy(t);
        }

        @Override
        public Object produce(final CreationalContext cc) {
            return delegate.produce(cc);
        }

        @Override
        public void dispose(final Object t) {
            delegate.dispose(t);
        }
    }

    private class ConfigBean implements Bean {

        private final Type t;
        private final Loader loader;
        private final String path;
        private final String name;

        ConfigBean(final Type t, String name, String path) {
            this.t = t;
            this.loader = "".equals(path) ? CdiConfigExtension.this.loader : PocConfigLoader.builder().path(path).build();
            this.path = path;
            this.name = name;
        }

        @Override
        public Class<?> getBeanClass() {
            return (Class<?>) t;
        }

        @Override
        public Set<InjectionPoint> getInjectionPoints() {
            return Collections.emptySet();
        }

        @Override
        public Object create(final CreationalContext creationalContext) {
            return loader.load(getBeanClass());
        }

        @Override
        public void destroy(final Object instance, final CreationalContext creationalContext) {
        }

        @Override
        public Set<Type> getTypes() {
            return Collections.singleton(t);
        }

        @Override
        public Set<Annotation> getQualifiers() {
            return Collections.singleton(new CdiDefaultAnnotation());
        }

        @Override
        public Class<? extends Annotation> getScope() {
            return Dependent.class;
        }

        @Override
        public String getName() {
            return name; //t.getTypeName();
        }

        @Override
        public Set<Class<? extends Annotation>> getStereotypes() {
            return Collections.emptySet();
        }

        @Override
        public boolean isAlternative() {
            return false;
        }

        public String path() {
            return path;
        }
    }

    /**
     * Auxiliary annotation for mocked beans used to cover injected injection points.
     */
    @SuppressWarnings("serial")
    public static final class CdiDefaultAnnotation extends AnnotationLiteral<Default> implements Default {
        private static final long serialVersionUID = 1L;
    }
}
