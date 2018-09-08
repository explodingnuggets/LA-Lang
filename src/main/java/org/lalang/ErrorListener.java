package org.lalang;

import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class ErrorListener implements ANTLRErrorListener {

	public static ErrorBuffer out;

    public ErrorListener(ErrorBuffer out) {
		this.out = out;
    }

    @Override
    public void syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		Token token = (Token)offendingSymbol;
		if(!out.modified()) {
			if(token.getType() == LALexer.ERRO) {
				if(token.getText().equals("{"))
					out.println("Linha " + (line+1) + ": comentario nao fechado");
				else
					out.println("Linha " + line + ": " + token.getText() + " - simbolo nao identificado");
			} else {
				if(token.getText() == "<EOF>")
					out.println("Linha " + line + ": " + "erro sintatico proximo a EOF");
				else
					out.println("Linha " + line + ": " + "erro sintatico proximo a " + token.getText());
			}
		}
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact,
			BitSet ambigAlts, ATNConfigSet configs) {
		
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex,
			BitSet conflictingAlts, ATNConfigSet configs) {
		
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction,
			ATNConfigSet configs) {
		
	}
}
