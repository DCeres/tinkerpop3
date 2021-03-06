package com.tinkerpop.gremlin.process.graph.traversal.step.filter;

import com.tinkerpop.gremlin.process.Traversal;
import com.tinkerpop.gremlin.process.traversal.step.Reversible;
import com.tinkerpop.gremlin.process.traverser.TraverserRequirement;
import com.tinkerpop.gremlin.process.traversal.util.TraversalHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class RetainStep<S> extends FilterStep<S> implements Reversible {

    private final String sideEffectKeyOrPathLabel;

    public RetainStep(final Traversal.Admin traversal, final String sideEffectKeyOrPathLabel) {
        super(traversal);
        this.sideEffectKeyOrPathLabel = sideEffectKeyOrPathLabel;
        this.setPredicate(traverser -> {
            final Object retain = traverser.asAdmin().getSideEffects().exists(this.sideEffectKeyOrPathLabel) ?
                    traverser.sideEffects(this.sideEffectKeyOrPathLabel) :
                    traverser.path(this.sideEffectKeyOrPathLabel);
            return retain instanceof Collection ?
                    ((Collection) retain).contains(traverser.get()) :
                    retain.equals(traverser.get());

        });
    }

    public RetainStep(final Traversal.Admin traversal, final Collection<S> retainCollection) {
        super(traversal);
        this.sideEffectKeyOrPathLabel = null;
        this.setPredicate(traverser -> retainCollection.contains(traverser.get()));
    }

    public RetainStep(final Traversal.Admin traversal, final S retainObject) {
        super(traversal);
        this.sideEffectKeyOrPathLabel = null;
        this.setPredicate(traverser -> retainObject.equals(traverser.get()));
    }

    public String toString() {
        return TraversalHelper.makeStepString(this, this.sideEffectKeyOrPathLabel);
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return null == this.sideEffectKeyOrPathLabel ?
                Collections.singleton(TraverserRequirement.OBJECT) :
                Stream.of(TraverserRequirement.OBJECT,
                        TraverserRequirement.SIDE_EFFECTS,
                        TraverserRequirement.PATH_ACCESS).collect(Collectors.toSet());
    }
}
