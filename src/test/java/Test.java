import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        final Terminal t = TerminalBuilder.builder()
                .system(true)
                .jna(true)
                .nativeSignals(true)
                .build();

        t.enterRawMode();

        int k;
        while((k = t.reader().read()) != -1) {
            System.out.println(k);
        }
    }
}
