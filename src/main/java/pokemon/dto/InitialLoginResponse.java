package pokemon.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class InitialLoginResponse {
    private String lt;
    private String execution;

    public String getLt() {
        return lt;
    }

    public String getExecution() {
        return execution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InitialLoginResponse that = (InitialLoginResponse) o;
        return Objects.equal(lt, that.lt) &&
                Objects.equal(execution, that.execution);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(lt, execution);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lt", lt)
                .add("execution", execution)
                .toString();
    }
}
