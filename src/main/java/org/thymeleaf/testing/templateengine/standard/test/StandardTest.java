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
package org.thymeleaf.testing.templateengine.standard.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.context.IContext;
import org.thymeleaf.fragment.IFragmentSpec;
import org.thymeleaf.testing.templateengine.resource.ITestResource;
import org.thymeleaf.testing.templateengine.testable.Test;
import org.thymeleaf.util.Validate;





public class StandardTest extends Test {

    public static final StandardTestValueType DEFAULT_VALUE_TYPE = StandardTestValueType.SPECIFIED; 
    
    
    private Map<String,StandardTestValueType> additionalInputsValueTypes = new HashMap<String, StandardTestValueType>(); 
    private StandardTestValueType contextValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType fragmentValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType inputValueType = StandardTestValueType.NO_VALUE;
    private StandardTestValueType inputCacheableValueType = StandardTestValueType.NO_VALUE;
    private StandardTestValueType cacheValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType nameValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType outputValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType templateModeValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType outputThrowableClassValueType = StandardTestValueType.NO_VALUE; 
    private StandardTestValueType outputThrowableMessagePatternValueType = StandardTestValueType.NO_VALUE; 
    
    
    
    public StandardTest() {
        super();
    }

    

    
    @Override
    public final void setOutput(final ITestResource output) {
        setOutput(output, DEFAULT_VALUE_TYPE);
    }
    
    @Override
    public final void setOutputThrowableClass(final Class<? extends Throwable> outputThrowableClass) {
        setOutputThrowableClass(outputThrowableClass, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setOutputThrowableMessagePattern(final String outputThrowableMessagePattern) {
        setOutputThrowableMessagePattern(outputThrowableMessagePattern, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setContext(final IContext context) {
        setContext(context, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setTemplateMode(final String templateMode) {
        setTemplateMode(templateMode, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setFragmentSpec(final IFragmentSpec fragmentSpec) {
        setFragmentSpec(fragmentSpec, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setInput(final ITestResource input) {
        setInput(input, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setAdditionalInputs(final Map<String, ITestResource> additionalInputs) {
        setAdditionalInputs(additionalInputs, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setAdditionalInput(final String name, ITestResource resource) {
        setAdditionalInput(name, resource, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setInputCacheable(final boolean inputCacheale) {
        setInputCacheable(inputCacheale, DEFAULT_VALUE_TYPE);
    }

    @Override
    public final void setName(final String name) {
        setName(name, DEFAULT_VALUE_TYPE);
    }



    
    
    public void setOutput(final ITestResource output, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setOutput(output);
        this.outputValueType = valueType;
    }

    
    public void setOutputThrowableClass(final Class<? extends Throwable> outputThrowableClass, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setOutputThrowableClass(outputThrowableClass);
        this.outputThrowableClassValueType = valueType;
    }

    
    public void setOutputThrowableMessagePattern(final String outputThrowableMessagePattern, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setOutputThrowableMessagePattern(outputThrowableMessagePattern);
        this.outputThrowableMessagePatternValueType = valueType;
    }

    public void setContext(final IContext context, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setContext(context);
        this.contextValueType = valueType;
    }

    public void setTemplateMode(final String templateMode, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setTemplateMode(templateMode);
        this.templateModeValueType = valueType;
    }

    public void setFragmentSpec(final IFragmentSpec fragmentSpec, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setFragmentSpec(fragmentSpec);
        this.fragmentValueType = valueType;
    }

    public void setInput(final ITestResource input, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setInput(input);
        this.inputValueType = valueType;
    }

    public void setAdditionalInputs(final Map<String, ITestResource> additionalInputs, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setAdditionalInputs(additionalInputs);
        if (additionalInputs != null) {
            for (final String inputName : additionalInputs.keySet()) {
                this.additionalInputsValueTypes.put(inputName, valueType);
            }
        }
    }

    public void setAdditionalInput(final String name, ITestResource resource, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setAdditionalInput(name, resource);
        this.additionalInputsValueTypes.put(name, valueType);
    }

    public void setInputCacheable(final boolean inputCacheale, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setInputCacheable(inputCacheale);
        this.inputCacheableValueType = valueType;
    }

    public void setName(final String name, final StandardTestValueType valueType) {
        Validate.notNull(valueType, "Value type cannot be null");
        super.setName(name);
        this.nameValueType = valueType;
    }



    

    public Map<String, StandardTestValueType> getAdditionalInputsValueTypes() {
        return Collections.unmodifiableMap(this.additionalInputsValueTypes);
    }

    public StandardTestValueType getAdditionalInputsValueTypes(final String inputName) {
        return this.additionalInputsValueTypes.get(inputName);
    }

    public StandardTestValueType getContextValueType() {
        return this.contextValueType;
    }

    public StandardTestValueType getFragmentValueType() {
        return this.fragmentValueType;
    }

    public StandardTestValueType getInputValueType() {
        return this.inputValueType;
    }

    public StandardTestValueType getInputCacheableValueType() {
        return this.inputCacheableValueType;
    }

    public StandardTestValueType getCacheValueType() {
        return this.cacheValueType;
    }

    public StandardTestValueType getNameValueType() {
        return this.nameValueType;
    }

    public StandardTestValueType getOutputValueType() {
        return this.outputValueType;
    }

    public StandardTestValueType getTemplateModeValueType() {
        return this.templateModeValueType;
    }

    public StandardTestValueType getOutputThrowableClassValueType() {
        return this.outputThrowableClassValueType;
    }

    public StandardTestValueType getOutputThrowableMessagePatternValueType() {
        return this.outputThrowableMessagePatternValueType;
    }
    
    
    
    
    
}