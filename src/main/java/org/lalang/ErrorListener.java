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

	StringBuffer content;
	boolean isModified = false;

    public ErrorListener(StringBuffer content) {
		this.content = content;
    }

    @Override
    public void syntaxError(Recognizer<?,?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        Token token = (Token)offendingSymbol;
		if(!isModified) {
			content.append("Linha " + line + ": ");
            if(token.getType() == LALexer.ERRO) {
				if(token.getText().equals("{")) {
					content.setLength(0);
					content.append("Linha " + (line+1) + ": comentario nao fechado\n");
				} else {
					content.append(token.getText() + " - simbolo nao identificado\n");
				}
			} else {
				content.append("erro sintatico proximo a ");
				if(token.getText() == "<EOF>")
					content.append("EOF\n");
				else
					content.append(token.getText() + "\n");
			}

			isModified=true;
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
