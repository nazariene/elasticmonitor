package ru.nazariene.elasticmonitor.service.condition;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import ru.nazariene.elasticmonitor.domain.Rule;
import ru.nazariene.elasticmonitor.util.TypeConverterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConditionVerificationService {

    public boolean verifyCondition(Rule rule, JsonNode searchResponse) {
        var condition = rule.getCondition();

        JsonNode jsonNodeFieldValue = getField(condition.getField(), searchResponse);
        var fieldValue = TypeConverterUtil.convert(jsonNodeFieldValue.asText(), condition.getFieldType());
        var expectedValue = TypeConverterUtil.convert(condition.getValue(), condition.getFieldType());

        var compareResult = ObjectUtils.compare(fieldValue, expectedValue);

        return calculateIfBreached(compareResult, condition.getOp());
    }

    private JsonNode getField(String fieldName, JsonNode document) {
        return document.at(fieldName);
    }

    private boolean calculateIfBreached(int compareResult, String op) {
        //Meh, too lazy to do this nicely
        if (compareResult == 0) {
            return !op.equals("eq") && !op.equals("gte") && !op.equals("lte");
        } else if (compareResult > 0) {
            return !op.equals("gte") && !op.equals("gt");
        } else {
            return !op.equals("lte") && !op.equals("lt");
        }
    }
}
