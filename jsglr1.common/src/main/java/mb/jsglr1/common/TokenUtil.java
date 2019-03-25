package mb.jsglr1.common;

import mb.common.region.Region;
import mb.common.token.Token;
import mb.common.token.*;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.*;

import java.util.ArrayList;

class TokenUtil {
    static ArrayList<Token> extract(IStrategoTerm ast) {
        final ImploderAttachment rootImploderAttachment = ImploderAttachment.get(ast);
        final ITokens tokens = rootImploderAttachment.getLeftToken().getTokenizer();
        final int tokenCount = tokens.getTokenCount();
        final ArrayList<Token> tokenStream = new ArrayList<>(tokenCount);
        int offset = -1;
        for(int i = 0; i < tokenCount; ++i) {
            final IToken jsglrToken = tokens.getTokenAt(i);
            if(tokens.isAmbigous() && jsglrToken.getStartOffset() < offset) {
                // In case of ambiguities, tokens inside the ambiguity are duplicated, ignore.
                continue;
            }
            if(jsglrToken.getStartOffset() > jsglrToken.getEndOffset()) {
                // Indicates an invalid region. Empty lists have regions like this.
                continue;
            }
            if(offset >= jsglrToken.getStartOffset()) {
                // Duplicate region, skip.
                continue;
            }
            offset = jsglrToken.getEndOffset();
            final Token token = convertToken(jsglrToken);
            tokenStream.add(token);
        }
        return tokenStream;
    }

    private static Token convertToken(IToken token) {
        final Region region = RegionUtil.fromToken(token);
        final TokenType tokenType = convertTokenKind(token.getKind());
        return new TokenImpl(region, tokenType, (IStrategoTerm) token.getAstNode());
    }

    private static TokenType convertTokenKind(int kind) {
        switch(kind) {
            case IToken.TK_IDENTIFIER:
                return TokenConstants.identifierType;
            case IToken.TK_STRING:
                return TokenConstants.stringType;
            case IToken.TK_NUMBER:
                return TokenConstants.numberType;
            case IToken.TK_KEYWORD:
                return TokenConstants.keywordType;
            case IToken.TK_OPERATOR:
                return TokenConstants.operatorType;
            case IToken.TK_LAYOUT:
                return TokenConstants.layoutType;
            default:
                return TokenConstants.unknownType;
        }
    }
}
