package io.github.andreabattaglia.fsm;

import java.util.ArrayList;
import java.util.List;

public class OrderContext implements FsmContext
{
    private final int amount;
    private final List<String> auditLog;
    private boolean shouldFail = false;

    public OrderContext(int amount)
    {
        this.amount = amount;
        this.auditLog = new ArrayList<>();
    }

	public boolean isShouldFail() {
		return shouldFail;
	}

	public void setShouldFail(boolean shouldFail) {
		this.shouldFail = shouldFail;
	}

	public int getAmount() {
		return amount;
	}

	public List<String> getAuditLog() {
		return auditLog;
	}
    
    
}
