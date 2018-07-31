package com.msxwtech.prototype.common.rsqlsupport;

import cz.jirutka.rsql.parser.ast.*;
import org.springframework.data.jpa.domain.Specification;

public class GenericRSQLVisitor<T> implements RSQLVisitor<Specification<T>, Void> {
    @Override
    public Specification<T> visit(AndNode andNode, Void aVoid) {
        return visit((LogicalNode)andNode, aVoid);
    }

    @Override
    public Specification<T> visit(OrNode orNode, Void aVoid) {
        return visit((LogicalNode)orNode, aVoid);
    }

    @Override
    public Specification<T> visit(ComparisonNode comparisonNode, Void aVoid) {
        return new GenericRSQLSpecification<T>(
                comparisonNode.getSelector(),
                comparisonNode.getOperator(),
                comparisonNode.getArguments()
        );
    }

    private Specification<T> visit(LogicalNode logicalNode, Void aVoid) {
        return logicalNode.getChildren().stream()
                .map(node -> node.accept(this))
                .reduce((specPrevious, specCurrent) -> logicalNode instanceof AndNode ?
                        specPrevious.and(specCurrent) :
                        specPrevious.or(specCurrent))
                .orElseThrow(() -> new RuntimeException("logic node must have more than two children node"));
    }
}
