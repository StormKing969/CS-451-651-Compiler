package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a conditional expression
 */

class JConditionalExpression extends JExpression {

    /** Test */
    private JExpression check;

    /** True Condition */
    private JExpression TrueCase;

    /** False Condition */
    private JExpression FalseCase;

    public JConditionalExpression(int line, JExpression TrueCase,
                        JExpression FalseCase, JExpression check) {
        super(line);
        this.TrueCase = TrueCase;
        this.FalseCase = FalseCase;
        this.check = check;
    }

    /**
     * Analyzing the if-statement means analyzing its components and checking
     * that the test is a boolean.
     *
     * @param context
     *            context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        check = (JExpression) check.analyze(context);
        check.type().mustMatchExpected(line(), Type.BOOLEAN);
        if (TrueCase != null && FalseCase != null) {
            TrueCase = (JExpression) TrueCase.analyze(context);
            FalseCase = (JExpression) FalseCase.analyze(context);
            type = TrueCase.type();
            FalseCase.type().mustMatchExpected(line(), type);
        }
        return this;
    }

    /**
     * Code generation for an if-statement. We generate code to branch over the
     * consequent if !test; the consequent is followed by an unconditonal branch
     * over (any) alternate.
     *
     * @param output
     *            the code emitter (basically an abstraction for producing the
     *            .class file).
     */

    public void codegen(CLEmitter output) {
        // Need two labels
        String trueLabel = output.createLabel();
        String falseLabel = output.createLabel();

        check.codegen(output, trueLabel, false);
        TrueCase.codegen(output);


        output.addBranchInstruction(GOTO, falseLabel);
        output.addLabel(trueLabel);

        FalseCase.codegen(output);
        output.addLabel(falseLabel);
    }

    /**
     * {@inheritDoc}
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JConditionalExpression line=\"%d\">\n", line());
        p.indentRight();

        p.printf("<Test>\n");
        p.indentRight();
        check.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Test>\n");

        p.printf("<True Condition>\n");
        p.indentRight();
        TrueCase.writeToStdOut(p);
        p.indentLeft();
        p.printf("</True Condition>\n");

        p.printf("<False Condition>\n");
        p.indentRight();
        FalseCase.writeToStdOut(p);
        p.indentLeft();
        p.printf("</False Condition>\n");

        p.indentLeft();
        p.printf("</JConditionalExpression>\n");
    }

}
