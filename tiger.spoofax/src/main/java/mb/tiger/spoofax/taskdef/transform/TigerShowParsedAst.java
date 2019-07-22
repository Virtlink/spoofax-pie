package mb.tiger.spoofax.taskdef.transform;

import mb.common.util.EnumSetView;
import mb.common.util.ListView;
import mb.jsglr.common.TermTracer;
import mb.jsglr1.common.JSGLR1ParseResult;
import mb.pie.api.ExecContext;
import mb.pie.api.Task;
import mb.pie.api.TaskDef;
import mb.resource.hierarchical.ResourcePath;
import mb.spoofax.core.language.transform.*;
import mb.stratego.common.StrategoUtil;
import mb.tiger.spoofax.taskdef.TigerParse;
import org.spoofax.interpreter.terms.IStrategoTerm;

import javax.inject.Inject;

public class TigerShowParsedAst implements TaskDef<TransformInput, TransformOutput>, TransformDef {
    private final TigerParse parse;


    @Inject public TigerShowParsedAst(TigerParse parse) {
        this.parse = parse;
    }


    @Override public String getId() {
        return getClass().getName();
    }

    @Override public TransformOutput exec(ExecContext context, TransformInput input) throws Exception {
        final TransformSubject subject = input.subject;
        final ResourcePath file = TransformSubjects.getFile(subject)
            .orElseThrow(() -> new RuntimeException("Cannot show parsed AST, subject '" + subject + "' is not a file subject"));

        final JSGLR1ParseResult parseOutput = context.require(parse, file);
        if(parseOutput.ast == null) {
            throw new RuntimeException("Cannot show parsed AST, parsed AST for '" + file + "' is null");
        }

        final IStrategoTerm term = TransformSubjects.caseOf(subject)
            .fileRegion((f, r) -> TermTracer.getSmallestTermEncompassingRegion(parseOutput.ast, r))
            .otherwise_(parseOutput.ast);

        final String formatted = StrategoUtil.toString(term);
        return new TransformOutput(ListView.of(TransformFeedbacks.openEditorWithText(formatted, "Parsed AST for '" + file + "'", null)));
    }

    @Override public Task<TransformOutput> createTask(TransformInput input) {
        return TaskDef.super.createTask(input);
    }


    @Override public String getDisplayName() {
        return "Show parsed AST";
    }

    @Override public EnumSetView<TransformExecutionType> getSupportedExecutionTypes() {
        return EnumSetView.of(TransformExecutionType.OneShot, TransformExecutionType.ContinuousOnEditor);
    }

    @Override public EnumSetView<TransformSubjectType> getSupportedSubjectTypes() {
        return EnumSetView.of(TransformSubjectType.File, TransformSubjectType.FileRegion);
    }
}
