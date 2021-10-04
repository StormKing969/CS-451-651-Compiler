package jminusminus;

import static jminusminus.CLConstants.GOTO;

public class JForStatement extends JStatement {

    private JStatement counter;

    private JExpression counter_limit;

    private JExpression counter_increment;

    private JStatement forLoopStatement;

    private LocalContext context;

    public JForStatement(int line, JStatement counter, JExpression counter_limit, JExpression counter_increment, JStatement forLoopStatement) {
        super(line);
        this.counter = counter;
        this.counter_limit = counter_limit;
        this.counter_increment = counter_increment;
        this.forLoopStatement = forLoopStatement;
    }

    public JStatement analyze(Context context) {
        this.context = new LocalContext(context);

        counter.analyze(context);
        counter_limit.analyze(context);
        counter_limit.type().mustMatchExpected(line(), Type.BOOLEAN);
        if (counter_increment != null) { 
            counter_increment.analyze(context);
        }

        forLoopStatement.analyze(context);

        return this;
    }

    public void codegen(CLEmitter output) {
        // Codegen counter
        counter.codegen(output);

        // Need two labels
        String test = output.createLabel();
        String out = output.createLabel();

        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        counter_limit.codegen(output, out, false);

        // Codegen forLoopStatement
        // Codegen counter_increment
        forLoopStatement.codegen(output);
        counter_increment.codegen(output);

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
    }

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForStatement line=\"%d\">\n", line());
        p.indentRight();

        p.printf("<CounterInitialise>\n");
        p.indentRight();
        if (counter != null) {
            counter.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</CounterInitialise>\n");

        p.printf("<CounterLimit>\n");
        p.indentRight();
        if (counter_limit != null) {
            counter_limit.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</CounterLimit>\n");

        p.printf("<CounterIncrement>\n");
        p.indentRight();
        if (counter_increment != null) {
            counter_increment.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</CounterIncrement>\n");

        p.printf("<Clause>\n");
        p.indentRight();
        if (forLoopStatement != null) {
            forLoopStatement.writeToStdOut(p);
        }
        forLoopStatement.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Clause>\n");

        p.indentLeft();
        p.printf("</JForStatement>\n");
    }

}
