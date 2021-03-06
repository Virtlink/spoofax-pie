package mb.jsglr.common;

import mb.common.region.Region;
import mb.common.token.Token;
import mb.common.token.TokenImpl;
import mb.common.token.TokenType;
import mb.common.token.TokenTypes;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.jsglr.client.imploder.IToken;
import org.spoofax.jsglr.client.imploder.ITokens;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

import java.util.ArrayList;

public class TokenUtil {
    public static ArrayList<Token<IStrategoTerm>> extract(IStrategoTerm ast) {
        final ImploderAttachment rootImploderAttachment = ImploderAttachment.get(ast);
        final ITokens tokens = rootImploderAttachment.getLeftToken().getTokenizer();
        final int tokenCount = tokens.getTokenCount();
        final ArrayList<Token<IStrategoTerm>> tokenStream = new ArrayList<>(tokenCount);
        int offset = -1;
        for(int i = 0; i < tokenCount; ++i) {
            final IToken jsglrToken = tokens.getTokenAt(i);
            if(tokens.isAmbiguous() && jsglrToken.getStartOffset() < offset) {
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
            final Token<IStrategoTerm> token = convertToken(jsglrToken);
            tokenStream.add(token);
        }
        return tokenStream;
    }

    private static Token<IStrategoTerm> convertToken(IToken token) {
        final TokenType tokenType = convertTokenKind(token.getKind());
        final Region region = RegionUtil.fromToken(token);
        final IStrategoTerm fragment = (IStrategoTerm) token.getAstNode();
        return new TokenImpl<>(tokenType, region, fragment);
    }

    private static TokenType convertTokenKind(int kind) {
        switch(kind) {
            case IToken.TK_IDENTIFIER:
                return TokenTypes.identifier();
            case IToken.TK_STRING:
                return TokenTypes.string();
            case IToken.TK_NUMBER:
                return TokenTypes.number();
            case IToken.TK_KEYWORD:
                return TokenTypes.keyword();
            case IToken.TK_OPERATOR:
                return TokenTypes.operator();
            case IToken.TK_LAYOUT:
                return TokenTypes.layout();
            default:
                return TokenTypes.unknown();
        }
    }
}
