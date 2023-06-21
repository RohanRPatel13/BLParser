import components.map.Map;
import components.program.Program;
import components.program.Program1;
import components.queue.Queue;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.statement.Statement;
import components.utilities.Reporter;
import components.utilities.Tokenizer;

/**
 * Layered implementation of secondary method {@code parse} for {@code Program}.
 *
 * @author Rohan Patel
 *
 */
public final class Program1Parse1 extends Program1 {

    /*
     * Private members --------------------------------------------------------
     */

    /**
     * Parses a single BL instruction from {@code tokens} returning the
     * instruction name as the value of the function and the body of the
     * instruction in {@code body}.
     *
     * @param tokens
     *            the input tokens
     * @param body
     *            the instruction body
     * @return the instruction name
     * @replaces body
     * @updates tokens
     * @requires <pre>
     * [<"INSTRUCTION"> is a prefix of tokens]  and
     *  [<Tokenizer.END_OF_INPUT> is a suffix of tokens]
     * </pre>
     * @ensures <pre>
     * if [an instruction string is a proper prefix of #tokens]  and
     *    [the beginning name of this instruction equals its ending name]  and
     *    [the name of this instruction does not equal the name of a primitive
     *     instruction in the BL language] then
     *  parseInstruction = [name of instruction at start of #tokens]  and
     *  body = [Statement corresponding to statement string of body of
     *          instruction at start of #tokens]  and
     *  #tokens = [instruction string at start of #tokens] * tokens
     * else
     *  [report an appropriate error message to the console and terminate client]
     * </pre>
     */
    private static String parseInstruction(Queue<String> tokens,
            Statement body) {
        assert tokens != null : "Violation of: tokens is not null";
        assert body != null : "Violation of: body is not null";
        assert tokens.length() > 0 && tokens.front().equals("INSTRUCTION") : ""
                + "Violation of: <\"INSTRUCTION\"> is proper prefix of tokens";
        String value = "";
        tokens.dequeue();
        value = tokens.dequeue();
        Reporter.assertElseFatalError(Tokenizer.isIdentifier(value),
                "Not a valid indentifier");
        Reporter.assertElseFatalError(tokens.dequeue().equals("IS"),
                "Missing IS after identifier");

        body.parseBlock(tokens);
        Reporter.assertElseFatalError(tokens.dequeue().equals("END"),
                "Missing END after identifier");
        Reporter.assertElseFatalError(tokens.dequeue().equals(value),
                "Identifiers are not the same");
        return value;

    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public Program1Parse1() {
        super();
    }

    /*
     * Public methods ---------------------------------------------------------
     */

    @Override
    public void parse(SimpleReader in) {
        assert in != null : "Violation of: in is not null";
        assert in.isOpen() : "Violation of: in.is_open";
        Queue<String> tokens = Tokenizer.tokens(in);
        this.parse(tokens);
    }

    @Override
    public void parse(Queue<String> tokens) {
        assert tokens != null : "Violation of: tokens is not null";
        assert tokens.length() > 0 : ""
                + "Violation of: Tokenizer.END_OF_INPUT is a suffix of tokens";

        Reporter.assertElseFatalError(tokens.dequeue().equals("PROGRAM"),
                "Missing PROGRAM at beginning");

        String programName = tokens.dequeue();

        Reporter.assertElseFatalError(Tokenizer.isIdentifier(programName),
                "Not a valid name");

        this.setName(programName);

        Reporter.assertElseFatalError(tokens.dequeue().equals("IS"),
                "Missing IS after identifier");

        String instructionName = "";
        Map<String, Statement> context = this.newContext();
        Statement s = this.newBody();

        while (tokens.front().equals("INSTRUCTION")) {
            instructionName = parseInstruction(tokens, s);
            Reporter.assertElseFatalError(!context.hasKey(instructionName),
                    "This instruction name already exists in the program");
            context.add(instructionName, s);
        }
        this.swapContext(context);
        Statement body = this.newBody();

        Reporter.assertElseFatalError(tokens.dequeue().equals("BEGIN"),
                "Missing BEGIN");

        body.parseBlock(tokens);
        this.swapBody(body);

        Reporter.assertElseFatalError(tokens.dequeue().equals("END"),
                "Missing END at the end of the program");
        Reporter.assertElseFatalError(tokens.dequeue().equals(programName),
                "Program names do not match");
        Reporter.assertElseFatalError(tokens.length() > 0,
                "Program names do not match");
    }

    /*
     * Main test method -------------------------------------------------------
     */

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        /*
         * Get input file name
         */
        out.print("Enter valid BL program file name: ");
        String fileName = in.nextLine();
        /*
         * Parse input file
         */
        out.println("*** Parsing input file ***");
        Program p = new Program1Parse1();
        SimpleReader file = new SimpleReader1L(fileName);
        Queue<String> tokens = Tokenizer.tokens(file);
        file.close();
        p.parse(tokens);
        /*
         * Pretty print the program
         */
        out.println("*** Pretty print of parsed program ***");
        p.prettyPrint(out);

        in.close();
        out.close();
    }

}
