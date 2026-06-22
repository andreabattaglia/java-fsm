package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.AbstractFsmCommandFactory;
import io.github.andreabattaglia.fsm.FsmCommand;
import io.github.andreabattaglia.fsm.NoOpFsmCommand;

import java.util.Map;

public class ResumeCommandFactory extends AbstractFsmCommandFactory<ResumeState, ResumeContext>
{
    public ResumeCommandFactory()
    {
        super(ResumeState.class);
    }

    @Override
    protected void registerCommands(Map<ResumeState, FsmCommand<ResumeState, ResumeContext>> target)
    {
        target.put(ResumeState.PENDING,     new PendingCommand());
        target.put(ResumeState.IN_PROGRESS, new InProgressCommand());
        target.put(ResumeState.DONE,        new NoOpFsmCommand<>(ResumeState.DONE));
        target.put(ResumeState.ABORTED,     new NoOpFsmCommand<>(ResumeState.ABORTED));
    }
}
