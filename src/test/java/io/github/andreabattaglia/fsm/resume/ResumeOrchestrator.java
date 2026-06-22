package io.github.andreabattaglia.fsm.resume;

import io.github.andreabattaglia.fsm.FsmOrchestratorService;

public class ResumeOrchestrator extends FsmOrchestratorService<ResumeState, ResumeContext>
{
    public ResumeOrchestrator()
    {
        super(new ResumeCommandFactory(), new ResumeTransitionPolicy(), new ResumeExceptionHandler());
    }
}
