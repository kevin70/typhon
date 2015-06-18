/*
 * Copyright 2013 The Skfiy Open Association.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.skfiy.typhon.dispatcher;

import org.skfiy.typhon.Container;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.skfiy.typhon.packet.Packet;

/**
 *
 * @author Kevin Zou <kevinz@skfiy.org>
 */
@Singleton
public class AsmDispatcherFactory extends ClassLoader
        implements DispatcherFactory, Opcodes {

    @Inject
    private Container container;
    private ActionHelper helper;
    private Dispatcher dispatcher;
    
    public AsmDispatcherFactory() {
        super(Dispatcher.class.getClassLoader());
    }
    
    @Override
    public synchronized Dispatcher getDispatcher() {
        if (dispatcher != null) {
            return dispatcher;
        }
        
        helper = new ActionHelper(container);
        String packageName = this.getClass().getPackage().getName();
        String simpleName = "__Typhon__AsmDispatcher__";
        String proxyClassName = packageName + "." + simpleName;
        String inertalName = proxyClassName.replaceAll("\\.", "/");
        
        ClassWriter cw = new ClassWriter(0);
        
        cw.visit(V1_7, ACC_PUBLIC, inertalName, null,
                Type.getInternalName(Object.class),
                new String[]{Type.getInternalName(Dispatcher.class)});

        Set<Class<?>> classes = helper.getActionMappings().keySet();
        // Fields
        for (Class clazz : classes) {
           cw.visitField(ACC_PRIVATE, buildFieldName(clazz), 
                   Type.getDescriptor(clazz), null, null).visitEnd();
        }

        MethodVisitor mv;
        // FIXME
        mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                "(" + Type.getDescriptor(Container.class) + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        
        // put field value
        for (Class clazz : classes) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(Type.getType(clazz));
            mv.visitMethodInsn(INVOKEINTERFACE,
                    Type.getInternalName(Container.class),
                    "getInstance", "(Ljava/lang/Class;)Ljava/lang/Object;");
            mv.visitTypeInsn(CHECKCAST, Type.getInternalName(clazz));
            mv.visitFieldInsn(PUTFIELD, inertalName, buildFieldName(clazz),
                    Type.getDescriptor(clazz));
        }
        
        mv.visitInsn(RETURN);
        mv.visitMaxs(4, 4);
        mv.visitEnd();
        
        // dispatch method
        mv = cw.visitMethod(ACC_PUBLIC, "dispatch",
                "(Ljava/lang/String;" + Type.getDescriptor(Packet.class) + ")V"
                , null, null);
        mv.visitCode();
        //
        Label endIfLab = new Label();
        for (Map.Entry<Class<?>, List<ActionMapping>> entry
                : helper.getActionMappings().entrySet()) {
            for (ActionMapping am : entry.getValue()) {
                mv.visitLdcInsn(am.getNs());
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String",
                        "equals", "(Ljava/lang/Object;)Z");
                
                Label lab = new Label();
                mv.visitJumpInsn(IFEQ, lab);
                
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, inertalName,
                        buildFieldName(entry.getKey()),
                        Type.getDescriptor(entry.getKey()));
                mv.visitVarInsn(ALOAD, 2);
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(am.getPacketClass()));
                mv.visitMethodInsn(INVOKEVIRTUAL,
                        Type.getInternalName(entry.getKey()),
                        am.getMethod().getName(),
                        "(" + Type.getDescriptor(am.getPacketClass()) + ")V");
                // 
                mv.visitJumpInsn(GOTO, endIfLab);
                mv.visitLabel(lab);
                mv.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
        
        // else
        String excepInternalName = Type.getInternalName(NoNamespaceDefException.class);
        mv.visitTypeInsn(NEW, excepInternalName);
        mv.visitInsn(DUP);
        
        // =====================================================================
        String sbInertalName = Type.getInternalName(StringBuilder.class);
        mv.visitTypeInsn(NEW, sbInertalName);
        mv.visitInsn(DUP);
        mv.visitLdcInsn("Not found \"");
        mv.visitMethodInsn(INVOKESPECIAL, sbInertalName, "<init>",
                "(Ljava/lang/String;)V");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, sbInertalName, "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitLdcInsn("\" namespace");
        mv.visitMethodInsn(INVOKEVIRTUAL, sbInertalName, "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
        mv.visitMethodInsn(INVOKEVIRTUAL, sbInertalName, "toString",
                "()Ljava/lang/String;");
        // =====================================================================
        mv.visitMethodInsn(INVOKESPECIAL, excepInternalName, "<init>",
                "(Ljava/lang/String;)V");
        mv.visitInsn(ATHROW);
        
        mv.visitLabel(endIfLab);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(RETURN);
        mv.visitMaxs(5, 4);
        mv.visitEnd();
        
        cw.visitEnd();
        
        // ------------------------------------------------------------------
        byte[] buf = cw.toByteArray();
        Class proxyClass = defineClass(proxyClassName, buf, 0, buf.length);
        
        try {
            
            dispatcher = (Dispatcher) proxyClass.
                    getConstructor(Container.class).newInstance(container);
        } catch (Exception e) {
            // throw new AssertionError("", e);
            e.printStackTrace();
        }
        return dispatcher;
    }

    @Override
    public Class<?> getPacketClass(String ns) {
        return helper.getPacketClass(ns);
    }
    
    private String buildFieldName(Class clazz) {
        return clazz.getCanonicalName().replaceAll("\\.", "_");
    }
    
}
