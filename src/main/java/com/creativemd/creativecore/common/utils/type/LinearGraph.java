package com.creativemd.creativecore.common.utils.type;

import java.util.HashMap;

public class LinearGraph extends Graph {
	
	public LinearGraph(HashMap<Float, Float> points) {
		super(points);
	}
	
	@Override
	public float getY(float x) {
		Float pointBefore = getPreviousPointX(x);
		Float pointAfter = getNextPointX(x);
		if (pointBefore != null && pointAfter != null) {
			if (pointBefore == pointAfter)
				return points.get(pointBefore);
			float distance = pointAfter - pointBefore;
			float relativePos = x - pointBefore;
			float percentage = relativePos / distance;
			float pointBeforeY = points.get(pointBefore);
			float distanceY = points.get(pointAfter) - pointBeforeY;
			return pointBeforeY + distanceY * percentage;
		}
		if (pointBefore != null)
			return points.get(pointBefore);
		if (pointAfter != null)
			return points.get(pointAfter);
		return 0;
	}
	
}
