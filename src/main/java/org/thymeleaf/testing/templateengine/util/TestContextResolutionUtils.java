/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2012, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.testing.templateengine.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ognl.Ognl;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.testing.templateengine.context.ITestContext;
import org.thymeleaf.testing.templateengine.context.ITestContextExpression;
import org.thymeleaf.testing.templateengine.engine.TestExecutor;
import org.thymeleaf.testing.templateengine.exception.TestEngineExecutionException;





public final class TestContextResolutionUtils {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    private static final String REQUEST_PARAMS_PREFIX = "param";
    private static final String REQUEST_ATTRS_PREFIX = "request";
    private static final String SESSION_ATTRS_PREFIX = "session";
    private static final String SERVLETCONTEXT_ATTRS_PREFIX = "application";
    
    
    public static IContext resolveTestContext(final ITestContext testContext) {
        
        if (testContext == null) {
            return null;
        }
        
        Locale locale = testContext.getLocale();
        if (locale == null) {
            locale = DEFAULT_LOCALE;
        }
        
        
        final Map<String,Object> variables = new HashMap<String, Object>();
        
        final Map<String,Object[]> requestParameters = new HashMap<String, Object[]>();
        variables.put(REQUEST_PARAMS_PREFIX, requestParameters);
        
        final Map<String,Object> requestAttributes = new HashMap<String, Object>();
        variables.put(REQUEST_ATTRS_PREFIX, requestAttributes);
        
        final Map<String,Object> sessionAttributes = new HashMap<String, Object>();
        variables.put(SESSION_ATTRS_PREFIX, sessionAttributes);
        
        final Map<String,Object> servletContextAttributes = new HashMap<String, Object>();
        variables.put(SERVLETCONTEXT_ATTRS_PREFIX, servletContextAttributes);

        
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getVariables().entrySet()) {
            resolve(entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression[]> entry : testContext.getRequestParameters().entrySet()) {
            
            final int firstPoint = entry.getKey().indexOf('.');
            final String paramName =
                    (firstPoint == -1? entry.getKey() : entry.getKey().substring(0, firstPoint));
            final String remainder = 
                    (firstPoint == -1? "" : entry.getKey().substring(firstPoint));
            final Object[] paramValues = new Object[entry.getValue().length];
            
            requestParameters.put(paramName, paramValues); // We initialize an array long enough to hold all the values.

            final int expressionsLen = entry.getValue().length;
            for (int i = 0; i < expressionsLen; i++) {
                resolve((REQUEST_PARAMS_PREFIX + "." + paramName + "[" + i + "]" + remainder), entry.getValue()[i], variables, locale);
            }
            
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getRequestAttributes().entrySet()) {
            resolve(REQUEST_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getSessionAttributes().entrySet()) {
            resolve(SESSION_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        for (final Map.Entry<String,ITestContextExpression> entry : testContext.getServletContextAttributes().entrySet()) {
            resolve(SERVLETCONTEXT_ATTRS_PREFIX + "." + entry.getKey(), entry.getValue(), variables, locale);
        }
        
        
        final ServletContext servletContext = createServletContext(servletContextAttributes);
        final HttpSession session = createHttpSession(servletContext,sessionAttributes);
        final HttpServletRequest request = createHttpServletRequest(session, requestAttributes, requestParameters, locale);
        final HttpServletResponse response = createHttpServletResponse();
        
        variables.remove(REQUEST_PARAMS_PREFIX);
        variables.remove(REQUEST_ATTRS_PREFIX);
        variables.remove(SESSION_ATTRS_PREFIX);
        variables.remove(SERVLETCONTEXT_ATTRS_PREFIX);
        
        return new WebContext(request, response, servletContext, locale, variables);
        
    }
    
    
    
    
    
    
    
    
    private TestContextResolutionUtils() {
        super();
    }
    
    

    
    
    private static final HttpServletRequest createHttpServletRequest(final HttpSession session, final Map<String,Object> attributes, final Map<String,Object[]> parameters, final Locale locale) {
        
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(request.getContextPath()).thenReturn("testing");
        Mockito.when(request.getLocale()).thenReturn(locale);
        Mockito.when(request.getLocales()).thenReturn(new ObjectEnumeration<Locale>(Arrays.asList(new Locale[]{locale})));
        
        Mockito.when(request.getSession()).thenReturn(session);
        Mockito.when(request.getSession(Matchers.anyBoolean())).thenReturn(session);

        Mockito.when(request.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributes));
        Mockito.when(request.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(attributes));
        Mockito.doAnswer(new SetAttributeAnswer(attributes)).when(request).setAttribute(Matchers.anyString(), Matchers.anyObject());
        
        Mockito.when(request.getParameterNames()).thenAnswer(new GetVariableNamesAnswer(parameters));
        Mockito.when(request.getParameterValues(Matchers.anyString())).thenAnswer(new GetParameterValuesAnswer(parameters));
        Mockito.when(request.getParameterMap()).thenAnswer(new GetParameterMapAnswer(parameters));
        Mockito.when(request.getParameter(Matchers.anyString())).thenAnswer(new GetParameterAnswer(parameters));

        
        return request;
        
    }
    

    
    private static final HttpSession createHttpSession(final ServletContext context, final Map<String,Object> attributes) {
        
        final HttpSession session = Mockito.mock(HttpSession.class);
        
        Mockito.when(session.getServletContext()).thenReturn(context);

        Mockito.when(session.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributes));
        Mockito.when(session.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(attributes));
        Mockito.doAnswer(new SetAttributeAnswer(attributes)).when(session).setAttribute(Matchers.anyString(), Matchers.anyObject());
        
        return session;
        
    }
    
    
    
    private static final HttpServletResponse createHttpServletResponse() {
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        return response;
    }

    
    
    private static final ServletContext createServletContext(final Map<String,Object> attributes) {
        
        final ServletContext servletContext = Mockito.mock(ServletContext.class);
        
        Mockito.when(servletContext.getAttributeNames()).thenAnswer(new GetVariableNamesAnswer(attributes));
        Mockito.when(servletContext.getAttribute(Matchers.anyString())).thenAnswer(new GetAttributeAnswer(attributes));
        Mockito.doAnswer(new SetAttributeAnswer(attributes)).when(servletContext).setAttribute(Matchers.anyString(), Matchers.anyObject());
        
        return servletContext;
    }
    

    
    
    
    
    
    private static class ObjectEnumeration<T> implements Enumeration<T> {

        private final Iterator<T> iterator;
        
        public ObjectEnumeration(final Collection<T> values) {
            super();
            this.iterator = values.iterator();
        }
        
        public boolean hasMoreElements() {
            return this.iterator.hasNext();
        }

        public T nextElement() {
            return this.iterator.next();
        }
        
    }
    

    
    
    private static class GetVariableNamesAnswer implements Answer<Enumeration<?>> {

        private final Map<String,?> values;
        
        public GetVariableNamesAnswer(final Map<String,?> values) {
            super();
            this.values = values;
        }
        
        public Enumeration<?> answer(final InvocationOnMock invocation) throws Throwable {
            return new ObjectEnumeration<String>(this.values.keySet());
        }
        
    }
    
    
    
    private static class GetAttributeAnswer implements Answer<Object> {

        private final Map<String,Object> values;
        
        public GetAttributeAnswer(final Map<String,Object> values) {
            super();
            this.values = values;
        }
        
        public Object answer(final InvocationOnMock invocation) throws Throwable {
            final String attributeName = (String) invocation.getArguments()[0];
            return this.values.get(attributeName);
        }
        
    }
    
    
    
    private static class SetAttributeAnswer implements Answer<Object> {

        private final Map<String,Object> values;
        
        public SetAttributeAnswer(final Map<String,Object> values) {
            super();
            this.values = values;
        }
        
        public Object answer(final InvocationOnMock invocation) throws Throwable {
            final String attributeName = (String) invocation.getArguments()[0];
            final Object attributeValue = invocation.getArguments()[1];
            this.values.put(attributeName, attributeValue);
            return null;
        }
        
    }
    
    
    
    private static class GetParameterValuesAnswer implements Answer<String[]> {

        private final Map<String,Object[]> values;
        
        public GetParameterValuesAnswer(final Map<String,Object[]> values) {
            super();
            this.values = values;
        }
        
        public String[] answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final Object[] parameterValues = this.values.get(parameterName);
            if (parameterValues == null) {
                return null;
            }
            final String[] parameterValuesArray = new String[parameterValues.length];
            for (int i = 0; i < parameterValuesArray.length; i++) {
                final Object value = parameterValues[i];
                parameterValuesArray[i] = (value == null? null : value.toString());
            }
            return parameterValuesArray;
        }
        
    }
    
    
    
    private static class GetParameterAnswer implements Answer<String> {

        private final Map<String,Object[]> values;
        
        public GetParameterAnswer(final Map<String,Object[]> values) {
            super();
            this.values = values;
        }
        
        public String answer(final InvocationOnMock invocation) throws Throwable {
            final String parameterName = (String) invocation.getArguments()[0];
            final Object[] parameterValues = this.values.get(parameterName);
            if (parameterValues == null) {
                return null;
            }
            final Object value = parameterValues[0];
            return (value == null? null : value.toString());
        }
        
    }
    
    
    
    private static class GetParameterMapAnswer implements Answer<Map<String,String[]>> {

        private final Map<String,Object[]> values;
        
        public GetParameterMapAnswer(final Map<String,Object[]> values) {
            super();
            this.values = values;
        }
        
        public Map<String,String[]> answer(final InvocationOnMock invocation) throws Throwable {
            final Map<String,String[]> parameterMap = new HashMap<String, String[]>();
            for (final Map.Entry<String,Object[]> valueEntry : this.values.entrySet()) {
                final String parameterName = valueEntry.getKey();
                final Object[] parameterValues = valueEntry.getValue();
                if (parameterValues == null) {
                    parameterMap.put(parameterName, null);
                    continue;
                }
                final String[] parameterValuesArray = new String[parameterValues.length];
                for (int i = 0; i < parameterValuesArray.length; i++) {
                    final Object value = parameterValues[i];
                    parameterValuesArray[i] = (value == null? null : value.toString());
                }
                parameterMap.put(parameterName, parameterValuesArray);
            }
            return parameterMap;
        }
        
    }
    
    

    private static void resolve(final String expression, final ITestContextExpression contextExpression, final Map<String,Object> variables, final Locale locale) {
        
        try {
            
            final Object result = contextExpression.evaluate(variables, locale);
            
            final Object parsedExpression = Ognl.parseExpression(expression);
            Ognl.setValue(parsedExpression, variables, result);

        } catch (final Throwable t) {
            throw new TestEngineExecutionException(
                    "Exception while trying to evaluate expression \"" +  expression + "\" on context for test \"" + TestExecutor.getThreadTestName() + "\"", t);
        }
        
    }
    
    
    
    
}
