package extract.source;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.stmt.*;
import property.SourceApiJavaProperty;
import property.SourceCodeJavaProperty;

import java.util.*;

public class SourceApiExtractor {

    private final Map<String, Map<String, MutableInteger>> apiClassMethodMap;
    private final Set<String> apiMethodNameSet;
    private final List<Map<String, Map<String, MutableInteger>>> instanceVariableMapList;
    private final List<Map<String, Map<String, MutableInteger>>> otherVariableMapList;
    private final Map<String, MutableInteger> apiClassConstructorMap;

    private int enumCounter;
    private int interfaceCounter;
    private int classCounter;
    private int bodyDeclarationCounter;
    private int enumConstantsCounter;
    private int methodCounter;
    private int bodyCounter;
    private int classFieldCounter;
    private int parameterCounter;
    private int statementCounter;
    private int expressionCounter;
    private int ifStatementCounter;
    private int forStatementCounter;
    private int forEachStatementCounter;
    private int doWhileStatementCounter;
    private int tryStatementCounter;
    private int assertStatementCounter;
    private int switchStatementCounter;
    private int synchronizedStatementCounter;
    private int constructorInvocationStatementCounter;
    private int variableStatementCounter;
    private int lambdaExpressionCounter;
    private int objectCreationExpressionCounter;
    private int fieldAccessExpressionCounter;
    private int arrayCreationExpressionCounter;
    private int assignExpressionCounter;
    private int binaryExpressionCounter;
    private int conditionalExpressionCounter;
    private int catchExpressionCounter;
    private int arrayInitializedObjectsCounter;
    private int initializedDeclarationCounter;
    private int constructorDeclarationCounter;
    private int returnStatementCounter;
    private int yieldStatementCounter;
    private int localClassDeclarationCounter;
    private int thrownStatementCounter;
    private int labeledStatementCounter;
    private int castExpressionCounter;
    private int enclosedExpressionCounter;
    private int unaryExpressionCounter;
    private int arrayAccessExpressionCounter;
    private int methodCallExpressionCounter;

    public SourceApiExtractor() {
        apiClassConstructorMap = Map.ofEntries(
                Map.entry("Intent", new MutableInteger()),
                Map.entry("IntentFilter", new MutableInteger()),
                Map.entry("DataInputStream", new MutableInteger()),
                Map.entry("DataOutputStream", new MutableInteger()),
                Map.entry("BufferedReader", new MutableInteger()),
                Map.entry("StringBuilder", new MutableInteger()),
                Map.entry("StringBuffer", new MutableInteger()),
                Map.entry("File", new MutableInteger()),
                Map.entry("Stream", new MutableInteger()),
                Map.entry("String", new MutableInteger())
        );
        apiClassMethodMap = Map.ofEntries(
                Map.entry("Intent", Map.ofEntries(
                        Map.entry("addFlags", new MutableInteger()),
                        Map.entry("setFlags", new MutableInteger()),
                        Map.entry("setDataAndType", new MutableInteger()),
                        Map.entry("putExtra", new MutableInteger())
                )),
                Map.entry("DataOutputStream", Map.ofEntries(
                        Map.entry("writeBytes", new MutableInteger())
                )),
                Map.entry("StringBuilder", Map.ofEntries(
                        Map.entry("append", new MutableInteger()),
                        Map.entry("indexOf", new MutableInteger()),
                        Map.entry("substring", new MutableInteger())
                )),
                Map.entry("StringBuffer", Map.ofEntries(
                        Map.entry("append", new MutableInteger()),
                        Map.entry("indexOf", new MutableInteger()),
                        Map.entry("substring", new MutableInteger())
                )),
                Map.entry("ContentResolver", Map.ofEntries(
                        Map.entry("query", new MutableInteger()),
                        Map.entry("insert", new MutableInteger()),
                        Map.entry("update", new MutableInteger())
                )),
                Map.entry("String", Map.ofEntries(
                        Map.entry("toLowerCase", new MutableInteger()),
                        Map.entry("trim", new MutableInteger()),
                        Map.entry("toUpperCase", new MutableInteger()),
                        Map.entry("charAt", new MutableInteger())
                ))
        );
        apiMethodNameSet = new HashSet<>(Math.max((int) (apiClassMethodMap.size()/.75f) + 1, 16));
        for (Map<String, MutableInteger> value : apiClassMethodMap.values()) {
            apiMethodNameSet.addAll(value.keySet());
        }
        instanceVariableMapList = new ArrayList<>();
        otherVariableMapList = new ArrayList<>();

        this.enumCounter = 0;
        this.interfaceCounter = 0;
        this.classCounter = 0;
        this.bodyDeclarationCounter = 0;
        this.enumConstantsCounter = 0;
        this.methodCounter = 0;
        this.bodyCounter = 0;
        this.classFieldCounter = 0;
        this.parameterCounter = 0;
        this.statementCounter = 0;
        this.expressionCounter = 0;
        this.ifStatementCounter = 0;
        this.forStatementCounter = 0;
        this.forEachStatementCounter = 0;
        this.doWhileStatementCounter = 0;
        this.tryStatementCounter = 0;
        this.assertStatementCounter = 0;
        this.switchStatementCounter = 0;
        this.synchronizedStatementCounter = 0;
        this.constructorInvocationStatementCounter = 0;
        this.variableStatementCounter = 0;
        this.lambdaExpressionCounter = 0;
        this.objectCreationExpressionCounter = 0;
        this.fieldAccessExpressionCounter = 0;
        this.arrayCreationExpressionCounter = 0;
        this.assignExpressionCounter = 0;
        this.binaryExpressionCounter = 0;
        this.conditionalExpressionCounter = 0;
        this.catchExpressionCounter = 0;
        this.arrayInitializedObjectsCounter = 0;
        this.initializedDeclarationCounter = 0;
        this.constructorDeclarationCounter = 0;
        this.returnStatementCounter = 0;
        this.yieldStatementCounter = 0;
        this.localClassDeclarationCounter = 0;
        this.thrownStatementCounter = 0;
        this.labeledStatementCounter = 0;
        this.castExpressionCounter = 0;
        this.enclosedExpressionCounter = 0;
        this.unaryExpressionCounter = 0;
        this.arrayAccessExpressionCounter = 0;
        this.methodCallExpressionCounter = 0;
    }

    /**
     * Returns count of method calls of the specific type
     *
     * @param type       class name
     * @param methodName method name to count
     * @return count of calls is we are track them, otherwise returns -1
     */
    public int getMethodCallCountOfType(String type, String methodName) {
        Map<String, MutableInteger> methodMap = apiClassMethodMap.get(type);
        if (methodMap == null) {
            return -1;
        }
        MutableInteger integer = methodMap.get(methodName);
        if (integer == null) {
            return -1;
        }
        return integer.intValue();
    }

    /**
     * Returns count of constructor calls of the specific type
     *
     * @param type class name
     * @return count of calls is we are track them, otherwise returns -1
     */
    public int getConstructorCallCountOfType(String type) {
        MutableInteger integer = apiClassConstructorMap.get(type);
        if (integer == null) {
            return -1;
        }
        return integer.intValue();
    }

    /**
     * Extracts information about api calls from CompilationUnit of java file
     * The extraction is mostly based on how Jadx decompiler generates code.
     * It means, that case when:
     * 1. variables are overlapping is out of scope;
     * 2. instance variables permitted to invoke without this is out of scope.
     *
     * @param cu Compilation unit of java file
     */
    public void extractInfo(CompilationUnit cu) {
        if (cu == null) {
            return;
        }
        Optional<String> name = cu.getPrimaryTypeName();
        if (name.isEmpty()) {
            return;
        }
        Optional<TypeDeclaration<?>> optionalTypeDeclaration = cu.getPrimaryType();
        if (optionalTypeDeclaration.isEmpty()) {
            return;
        }
        TypeDeclaration<?> typeDeclaration = optionalTypeDeclaration.get();
        if (typeDeclaration.isClassOrInterfaceDeclaration()) {
            extractClassOrInterfaceDeclaration(typeDeclaration.asClassOrInterfaceDeclaration());
        } else if (typeDeclaration.isEnumDeclaration()) {
            extractEnumDeclaration(typeDeclaration.asEnumDeclaration());
        }
    }

    /**
     * Clears counters value that parsed before
     */
    public void clearCounters() {
        for (Map<String, MutableInteger> methods : apiClassMethodMap.values()) {
            for (MutableInteger counter : methods.values()) {
                counter.clear();
            }
        }
    }

    /**
     * Export parameters in property object
     *
     * @param apiProperty object which receives properties
     */
    public void exportInProperties(SourceApiJavaProperty apiProperty, SourceCodeJavaProperty codeProperty) {
        exportInApiProperty(apiProperty);
        exportInCodeProperty(codeProperty);
    }

    private void exportInApiProperty(SourceApiJavaProperty apiProperty) {
        apiProperty.setIntentAddFlags(getMethodCallCountOfType("Intent", "addFlags"));
        apiProperty.setIntentSetFlags(getMethodCallCountOfType("Intent", "setFlags"));
        apiProperty.setIntentSetDataAndType(getMethodCallCountOfType("Intent", "setDataAndType"));
        apiProperty.setIntentPutExtra(getMethodCallCountOfType("Intent", "putExtra"));
        apiProperty.setIntentConstructor(apiClassConstructorMap.get("Intent").intValue());
        apiProperty.setIntentFilterConstructor(apiClassConstructorMap.get("IntentFilter").intValue());
        apiProperty.setDataInputStreamConstructor(apiClassConstructorMap.get("DataInputStream").intValue());
        apiProperty.setDataOutputStreamWriteBytes(getMethodCallCountOfType("DataOutputStream", "writeBytes"));
        apiProperty.setDataOutputStreamConstructor(apiClassConstructorMap.get("DataOutputStream").intValue());
        apiProperty.setBufferedReaderConstructor(apiClassConstructorMap.get("BufferedReader").intValue());
        apiProperty.setStringBuilderAppend(getMethodCallCountOfType("StringBuilder", "append"));
        apiProperty.setStringBuilderIndexOf(getMethodCallCountOfType("StringBuilder", "indexOf"));
        apiProperty.setStringBuilderSubstring(getMethodCallCountOfType("StringBuilder", "substring"));
        apiProperty.setStringBuilderConstructor(apiClassConstructorMap.get("StringBuilder").intValue());
        apiProperty.setStringBufferAppend(getMethodCallCountOfType("StringBuffer", "append"));
        apiProperty.setStringBufferIndexOf(getMethodCallCountOfType("StringBuffer", "indexOf"));
        apiProperty.setStringBufferSubstring(getMethodCallCountOfType("StringBuffer", "substring"));
        apiProperty.setStringBufferConstructor(apiClassConstructorMap.get("StringBuffer").intValue());
        apiProperty.setContentResolverQuery(getMethodCallCountOfType("ContentResolver", "query"));
        apiProperty.setContentResolverInsert(getMethodCallCountOfType("ContentResolver", "insert"));
        apiProperty.setContentResolverUpdate(getMethodCallCountOfType("ContentResolver", "update"));
        apiProperty.setStringConstructor(apiClassConstructorMap.get("String").intValue());
        apiProperty.setStringToLowerCase(getMethodCallCountOfType("String", "toLowerCase"));
        apiProperty.setStringToUpperCase(getMethodCallCountOfType("String", "toUpperCase"));
        apiProperty.setStringTrim(getMethodCallCountOfType("String", "trim"));
        apiProperty.setStringCharAt(getMethodCallCountOfType("String", "charAt"));
        apiProperty.setFileConstructor(apiClassConstructorMap.get("File").intValue());
        apiProperty.setStreamConstructor(apiClassConstructorMap.get("Stream").intValue());
    }

    private void exportInCodeProperty(SourceCodeJavaProperty codeProperty) {
        codeProperty.setEnums(enumCounter);
        codeProperty.setInterfaces(interfaceCounter);
        codeProperty.setClasses(classCounter);
        codeProperty.setBodyDeclarations(bodyDeclarationCounter);
        codeProperty.setEnumConstants(enumConstantsCounter);
        codeProperty.setMethods(methodCounter);
        codeProperty.setBodies(bodyCounter);
        codeProperty.setClassFields(classFieldCounter);
        codeProperty.setParameters(parameterCounter);
        codeProperty.setStatements(statementCounter);
        codeProperty.setExpressions(expressionCounter);
        codeProperty.setIfStatements(ifStatementCounter);
        codeProperty.setForStatements(forStatementCounter);
        codeProperty.setForEachStatements(forEachStatementCounter);
        codeProperty.setDoWhileStatements(doWhileStatementCounter);
        codeProperty.setTryStatements(tryStatementCounter);
        codeProperty.setAssertStatements(assertStatementCounter);
        codeProperty.setSwitchStatements(switchStatementCounter);
        codeProperty.setSynchronizedStatements(synchronizedStatementCounter);
        codeProperty.setConstructorInvocationStatements(constructorInvocationStatementCounter);
        codeProperty.setVariableStatements(variableStatementCounter);
        codeProperty.setLambdaExpressions(lambdaExpressionCounter);
        codeProperty.setObjectCreationExpressions(objectCreationExpressionCounter);
        codeProperty.setFieldAccessExpressions(fieldAccessExpressionCounter);
        codeProperty.setArrayCreationExpressions(arrayCreationExpressionCounter);
        codeProperty.setAssignExpressions(assignExpressionCounter);
        codeProperty.setBinaryExpressions(binaryExpressionCounter);
        codeProperty.setConditionalExpressions(conditionalExpressionCounter);
        codeProperty.setCatchExpressions(catchExpressionCounter);
        codeProperty.setArrayInitializedObjects(arrayInitializedObjectsCounter);
        codeProperty.setInitializedDeclarations(initializedDeclarationCounter);
        codeProperty.setConstructorDeclarations(constructorDeclarationCounter);
        codeProperty.setReturnStatements(returnStatementCounter);
        codeProperty.setYieldStatements(yieldStatementCounter);
        codeProperty.setLocalClassDeclarations(localClassDeclarationCounter);
        codeProperty.setThrownStatements(thrownStatementCounter);
        codeProperty.setLabeledStatements(labeledStatementCounter);
        codeProperty.setCastExpressions(castExpressionCounter);
        codeProperty.setEnclosedExpressions(enclosedExpressionCounter);
        codeProperty.setUnaryExpressions(unaryExpressionCounter);
        codeProperty.setArrayAccessExpressions(arrayAccessExpressionCounter);
        codeProperty.setMethodCallExpressions(methodCallExpressionCounter);
    }

    private void extractClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration declaration) {
        if (declaration.isInterface()) {
            extractInterfaceDeclaration(declaration);
        } else {
            extractClassDeclaration(declaration);
        }
    }

    private void extractEnumDeclaration(EnumDeclaration enumDeclaration) {
        ++enumCounter;
        instanceVariableMapList.add(new HashMap<>());
        otherVariableMapList.add(new HashMap<>());
        for (BodyDeclaration<?> declaration : enumDeclaration.getMembers()) {
            extractBodyDeclaration(declaration);
        }
        for (EnumConstantDeclaration constantDeclaration : enumDeclaration.getEntries()) {
            extractEnumConstantDeclaration(constantDeclaration);
        }
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
        instanceVariableMapList.remove(instanceVariableMapList.size() - 1);
    }

    private void extractInterfaceDeclaration(ClassOrInterfaceDeclaration interfaceDeclaration) {
        ++interfaceCounter;
        otherVariableMapList.add(new HashMap<>());
        for (BodyDeclaration<?> declaration : interfaceDeclaration.getMembers()) {
            extractBodyDeclaration(declaration);
        }
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractClassDeclaration(ClassOrInterfaceDeclaration classDeclaration) {
        ++classCounter;
        instanceVariableMapList.add(new HashMap<>());
        otherVariableMapList.add(new HashMap<>());
        for (BodyDeclaration<?> declaration : classDeclaration.getMembers()) {
            extractBodyDeclaration(declaration);
        }
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
        instanceVariableMapList.remove(instanceVariableMapList.size() - 1);
    }

    private void extractBodyDeclaration(BodyDeclaration<?> bodyDeclaration) {
        ++bodyDeclarationCounter;
        if (bodyDeclaration.isMethodDeclaration()) {
            extractMethod(bodyDeclaration.asMethodDeclaration());
        } else if (bodyDeclaration.isClassOrInterfaceDeclaration()) {
            extractClassOrInterfaceDeclaration(bodyDeclaration.asClassOrInterfaceDeclaration());
        } else if (bodyDeclaration.isEnumDeclaration()) {
            extractEnumDeclaration(bodyDeclaration.asEnumDeclaration());
        } else if (bodyDeclaration.isConstructorDeclaration()) {
            ++constructorDeclarationCounter;
            extractBody(bodyDeclaration.asConstructorDeclaration().getBody());
        } else if (bodyDeclaration.isFieldDeclaration()) {
            extractClassField(bodyDeclaration.asFieldDeclaration());
        } else if (bodyDeclaration.isInitializerDeclaration()) {
            ++initializedDeclarationCounter;
            extractBody(bodyDeclaration.asInitializerDeclaration().getBody());
        }
    }

    private void extractEnumConstantDeclaration(EnumConstantDeclaration constantDeclaration) {
        ++enumConstantsCounter;
        otherVariableMapList.add(new HashMap<>());
        for (BodyDeclaration<?> bodyDeclaration : constantDeclaration.getClassBody()) {
            if (bodyDeclaration.isMethodDeclaration()) {
                extractMethod(bodyDeclaration.asMethodDeclaration());
            }
        }
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractMethod(MethodDeclaration method) {
        ++methodCounter;
        Optional<BlockStmt> optionalBody = method.getBody();
        if (optionalBody.isEmpty()) {
            return;
        }
        otherVariableMapList.add(new HashMap<>());
        extractParameters(method.getParameters());
        extractBody(optionalBody.get());

        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractBody(BlockStmt body) {
        ++bodyCounter;
        otherVariableMapList.add(new HashMap<>());
        for (Statement statement : body.getStatements()) {
            extractStatement(statement);
        }
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractClassField(FieldDeclaration fieldDeclaration) {
        ++classFieldCounter;
        NodeList<VariableDeclarator> variables = fieldDeclaration.getVariables();
        if (fieldDeclaration.isStatic()) {
            Map<String, Map<String, MutableInteger>> classVariableMap = otherVariableMapList.get(
                    otherVariableMapList.size() - 1
            );
            putVariablesToMap(variables, classVariableMap);
        } else {
            Map<String, Map<String, MutableInteger>> instanceVariableMap = instanceVariableMapList.get(
                    instanceVariableMapList.size() - 1
            );
            putVariablesToMap(variables, instanceVariableMap);
        }
    }

    private void extractParameters(List<Parameter> parameters) {
        ++parameterCounter;
        Map<String, Map<String, MutableInteger>> variablesMap = otherVariableMapList.get(
                otherVariableMapList.size() - 1
        );
        for (Parameter parameter : parameters) {
            extractParameter(parameter, variablesMap);
        }
    }

    private void extractStatement(Statement statement) {
        ++statementCounter;
        /* Out of scope: breakStatement, continueStatement, emptyStatement, unparsableStatement */
        if (statement.isExpressionStmt()) {
            extractExpression(statement.asExpressionStmt().getExpression());
        } else if (statement.isBlockStmt()) {
            extractBody(statement.asBlockStmt());
        } else if (statement.isIfStmt()) {
            extractIfStatement(statement.asIfStmt());
        } else if (statement.isForStmt()) {
            extractForStatement(statement.asForStmt());
        } else if (statement.isForEachStmt()) {
            extractForEachStatement(statement.asForEachStmt());
        } else if (statement.isDoStmt()) {
            extractDoWhileStatement(statement.asDoStmt());
        } else if (statement.isWhileStmt()) {
            extractDoWhileStatement(statement.asWhileStmt());
        } else if (statement.isTryStmt()) {
            extractTryStatement(statement.asTryStmt());
        } else if (statement.isAssertStmt()) {
            extractAssertStatement(statement.asAssertStmt());
        } else if (statement.isLabeledStmt()) {
            ++labeledStatementCounter;
            extractStatement(statement.asLabeledStmt().getStatement());
        } else if (statement.isReturnStmt()) {
            ++returnStatementCounter;
            statement.asReturnStmt().getExpression().ifPresent(this::extractExpression);
        } else if (statement.isSwitchStmt()) {
            extractSwitchStatement(statement.asSwitchStmt());
        } else if (statement.isSynchronizedStmt()) {
            extractSynchronizedStatement(statement.asSynchronizedStmt());
        } else if (statement.isThrowStmt()) {
            ++thrownStatementCounter;
            extractExpression(statement.asThrowStmt().getExpression());
        } else if (statement.isExplicitConstructorInvocationStmt()) {
            extractExplicitConstructorInvocationStatement(statement.asExplicitConstructorInvocationStmt());
        } else if (statement.isLocalClassDeclarationStmt()) {
            ++localClassDeclarationCounter;
            extractClassDeclaration(statement.asLocalClassDeclarationStmt().getClassDeclaration());
        } else if (statement.isYieldStmt()) {
            ++yieldStatementCounter;
            extractExpression(statement.asYieldStmt().getExpression());
        }
    }

    private void putVariablesToMap(List<VariableDeclarator> variables,
                                   Map<String, Map<String, MutableInteger>> variablesMap) {
        for (VariableDeclarator variable : variables) {

            String type = variable.getTypeAsString();
            Map<String, MutableInteger> methodsInvocationMap = apiClassMethodMap.get(type);
            if (methodsInvocationMap != null) {
                variablesMap.put(variable.getNameAsString(), methodsInvocationMap);
            }
            variable.getInitializer().ifPresent(this::extractExpression);
        }
    }

    private void extractParameter(Parameter parameter, Map<String, Map<String, MutableInteger>> variablesMap) {
        ++parameterCounter;
        String type = parameter.getTypeAsString();
        Map<String, MutableInteger> methodsInvocationMap = apiClassMethodMap.get(type);
        if (methodsInvocationMap != null) {
            variablesMap.put(parameter.getNameAsString(), methodsInvocationMap);
        }
    }

    private void extractExpression(Expression expression) {
        ++expressionCounter;
        /* Out of scope: AnnotationExpr, NameExpr, UnaryExpr, ThisExpr, ArrayInitializerExpr, BooleanLiteralExpr,
            CharLiteralExpr, ClassExpr, DoubleLiteralExpr, InstanceOfExpr, IntegerLiteralExpr, LiteralExpr,
            LiteralStringValueExpr, LongLiteralExpr, MarkerAnnotationExpr, MethodReferenceExpr, NormalAnnotationExpr,
            NullLiteralExpr, SingleMemberAnnotationExpr, StringLiteralExpr, SuperExpr, SwitchExpr, TextBlockLiteralExpr,
            TypeExpr */
        if (expression.isVariableDeclarationExpr()) {
            extractVariableDeclaration(expression.asVariableDeclarationExpr());
        } else if (expression.isLambdaExpr()) {
            extractLambdaExpression(expression.asLambdaExpr());
        } else if (expression.isObjectCreationExpr()) {
            extractObjectCreationExpression(expression.asObjectCreationExpr());
        } else if (expression.isFieldAccessExpr()) {
            extractFieldAccessExpression(expression.asFieldAccessExpr());
        } else if (expression.isMethodCallExpr()) {
            extractMethodCallExpression(expression.asMethodCallExpr());
        } else if (expression.isArrayAccessExpr()) {
            extractArrayAccessExpression(expression.asArrayAccessExpr());
        } else if (expression.isArrayCreationExpr()) {
            extractArrayCreationExpression(expression.asArrayCreationExpr());
        } else if (expression.isAssignExpr()) {
            extractAssignExpression(expression.asAssignExpr());
        } else if (expression.isBinaryExpr()) {
            extractBinaryExpression(expression.asBinaryExpr());
        } else if (expression.isCastExpr()) {
            ++castExpressionCounter;
            extractExpression(expression.asCastExpr().getExpression());
        } else if (expression.isConditionalExpr()) {
            extractConditionalExpression(expression.asConditionalExpr());
        } else if (expression.isEnclosedExpr()) {
            ++enclosedExpressionCounter;
            extractExpression(expression.asEnclosedExpr().getInner());
        } else if (expression.isUnaryExpr()) {
            ++unaryExpressionCounter;
            extractExpression(expression.asUnaryExpr().getExpression());
        }
    }

    private void extractIfStatement(IfStmt ifStmt) {
        ++ifStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        extractExpression(ifStmt.getCondition());
        extractStatement(ifStmt.getThenStmt());
        ifStmt.getElseStmt().ifPresent(this::extractStatement);
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractForStatement(ForStmt forStmt) {
        ++forStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        for (Expression expression : forStmt.getInitialization()) {
            if (expression.isVariableDeclarationExpr()) {
                extractVariableDeclaration(expression.asVariableDeclarationExpr());
            }
        }
        extractStatement(forStmt.getBody());
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractForEachStatement(ForEachStmt forEachStmt) {
        ++forEachStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        extractVariableDeclaration(forEachStmt.getVariable());
        extractStatement(forEachStmt.getBody());
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    @SuppressWarnings("rawtypes")
    private <T extends NodeWithBody> void extractDoWhileStatement(T stmt) {
        ++doWhileStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        extractStatement(stmt.getBody());
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractTryStatement(TryStmt tryStmt) {
        ++tryStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        for (Expression expression : tryStmt.getResources()) {
            extractExpression(expression);
        }
        extractBody(tryStmt.getTryBlock());
        for (CatchClause catchClause : tryStmt.getCatchClauses()) {
            extractCatchClause(catchClause);
        }
        Optional<BlockStmt> optionalFinalBlock = tryStmt.getFinallyBlock();
        optionalFinalBlock.ifPresent(this::extractBody);
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractAssertStatement(AssertStmt assertStmt) {
        ++assertStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        extractExpression(assertStmt.getCheck());
        assertStmt.getMessage().ifPresent(this::extractExpression);
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractSwitchStatement(SwitchStmt switchStmt) {
        ++switchStatementCounter;
        extractExpression(switchStmt.getSelector());
        for (SwitchEntry switchEntry : switchStmt.getEntries()) {
            otherVariableMapList.add(new HashMap<>());
            for (Statement statement : switchEntry.getStatements()) {
                extractStatement(statement);
            }
            otherVariableMapList.remove(otherVariableMapList.size() - 1);
        }
    }

    private void extractSynchronizedStatement(SynchronizedStmt synchronizedStmt) {
        ++synchronizedStatementCounter;
        otherVariableMapList.add(new HashMap<>());
        extractExpression(synchronizedStmt.getExpression());
        extractBody(synchronizedStmt.getBody());
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractExplicitConstructorInvocationStatement(ExplicitConstructorInvocationStmt statement) {
        ++constructorInvocationStatementCounter;
        for (Expression expression : statement.asExplicitConstructorInvocationStmt().getArguments()) {
            extractExpression(expression);
        }
    }

    private void extractVariableDeclaration(VariableDeclarationExpr variableDeclarationExpr) {
        ++variableStatementCounter;
        Map<String, Map<String, MutableInteger>> variablesMap = otherVariableMapList.get(
                otherVariableMapList.size() - 1
        );
        putVariablesToMap(variableDeclarationExpr.getVariables(), variablesMap);
    }

    private void extractLambdaExpression(LambdaExpr lambdaExpression) {
        ++lambdaExpressionCounter;
        otherVariableMapList.add(new HashMap<>());
        extractParameters(lambdaExpression.getParameters());
        Statement body = lambdaExpression.getBody();
        if (body.isExpressionStmt()) {
            extractExpression(body.asExpressionStmt().getExpression());
        } else {
            extractBody(body.asBlockStmt());
        }
        otherVariableMapList.remove(otherVariableMapList.size() - 1);
    }

    private void extractObjectCreationExpression(ObjectCreationExpr expr) {
        ++objectCreationExpressionCounter;
        for (Expression argument : expr.getArguments()) {
            extractExpression(argument);
        }
        Optional<NodeList<BodyDeclaration<?>>> optionalBodyDeclarations = expr.getAnonymousClassBody();
        if (optionalBodyDeclarations.isPresent()) {
            for (BodyDeclaration<?> declaration : optionalBodyDeclarations.get()) {
                extractBodyDeclaration(declaration);
            }
        }
        expr.getScope().ifPresent(this::extractExpression);
        String type = expr.getTypeAsString();
        MutableInteger counter = apiClassConstructorMap.get(type);
        if (counter != null) {
            counter.increment();
        }
    }

    private void extractFieldAccessExpression(FieldAccessExpr fieldAccessExpr) {
        ++fieldAccessExpressionCounter;
        extractExpression(fieldAccessExpr.getScope());
    }

    private void extractMethodCallExpression(MethodCallExpr expr) {
        ++methodCallExpressionCounter;
        String methodName = expr.getNameAsString();
        for (Expression argument : expr.getArguments()) {
            extractExpression(argument);
        }
        expr.getScope().ifPresent(this::extractExpression);
        if (!apiMethodNameSet.contains(methodName)) {
            return;
        }
        Optional<Expression> optionalMethodScopeExpression = expr.getScope();
        if (optionalMethodScopeExpression.isEmpty()) {
            return;
        }
        Expression methodScopeExpression = optionalMethodScopeExpression.get();
        if (methodScopeExpression.isNameExpr()) {

            String variableName = methodScopeExpression.asNameExpr().getNameAsString();
            parseVariableAndMethod(variableName, methodName, otherVariableMapList);
        } else if (methodScopeExpression.isFieldAccessExpr()) {

            FieldAccessExpr methodScope = methodScopeExpression.asFieldAccessExpr();
            String variableName = methodScope.getNameAsString();
            Expression variableScope = methodScope.getScope();

            // Check if variable with name variableName is instance variable
            if (variableScope.isNameExpr() && variableScope.asNameExpr().getNameAsString().equals("this")
                    && parseVariableAndMethod(variableName, methodName, instanceVariableMapList)) {
                return;
            }
            parseVariableAndMethod(variableName, methodName, otherVariableMapList);
        }
    }

    private void extractArrayAccessExpression(ArrayAccessExpr arrayAccessExpr) {
        ++arrayAccessExpressionCounter;
        extractExpression(arrayAccessExpr.getName());
        extractExpression(arrayAccessExpr.getIndex());
    }

    private void extractArrayCreationExpression(ArrayCreationExpr arrayCreationExpr) {
        ++arrayCreationExpressionCounter;
        Optional<ArrayInitializerExpr> optionalArrayInitializerExpr = arrayCreationExpr.getInitializer();
        if (optionalArrayInitializerExpr.isEmpty()) {
            return;
        }
        String type = arrayCreationExpr.getElementType().asString();
        MutableInteger counter = apiClassConstructorMap.get(type);
        if (counter == null) {
            return;
        }
        counter.add(getCountInitializedObjects(optionalArrayInitializerExpr.get()));
    }

    private void extractAssignExpression(AssignExpr assignExpr) {
        ++assignExpressionCounter;
        extractExpression(assignExpr.getTarget());
        extractExpression(assignExpr.getValue());
    }

    private void extractBinaryExpression(BinaryExpr binaryExpr) {
        ++binaryExpressionCounter;
        extractExpression(binaryExpr.getLeft());
        extractExpression(binaryExpr.getRight());
    }

    private void extractConditionalExpression(ConditionalExpr conditionalExpr) {
        ++conditionalExpressionCounter;
        extractExpression(conditionalExpr.getCondition());
        extractExpression(conditionalExpr.getElseExpr());
        extractExpression(conditionalExpr.getThenExpr());
    }

    private void extractCatchClause(CatchClause catchClause) {
        ++catchExpressionCounter;
        Map<String, Map<String, MutableInteger>> variablesMap = otherVariableMapList.get(
                otherVariableMapList.size() - 1
        );
        extractParameter(catchClause.getParameter(), variablesMap);
        extractBody(catchClause.getBody());
    }

    private static boolean parseVariableAndMethod(String variableName, String methodName,
                                                  List<Map<String, Map<String, MutableInteger>>> variables) {
        for (Map<String, Map<String, MutableInteger>> variable : variables) {
            Map<String, MutableInteger> objectMethods = variable.get(variableName);
            if (objectMethods == null) {
                continue;
            }
            MutableInteger counter = objectMethods.get(methodName);
            if (counter != null) {
                counter.increment();
                return true;
            }
        }
        return false;
    }

    private int getCountInitializedObjects(ArrayInitializerExpr arrayInitializerExpr) {
        ++arrayInitializedObjectsCounter;
        int counter = 0;
        for (Expression value : arrayInitializerExpr.getValues()) {
            if (value.isArrayInitializerExpr()) {
                counter += getCountInitializedObjects(arrayInitializerExpr);
            }
            counter += 1;
        }
        return counter;
    }
}
