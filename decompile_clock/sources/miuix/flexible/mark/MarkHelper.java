package miuix.flexible.mark;

import android.view.View;
import java.util.Arrays;
import java.util.Comparator;

/* JADX INFO: loaded from: classes2.dex */
public class MarkHelper {

    public interface IParamsGetter {
        float getGroupWeight(View view);

        int getMark(View view);

        int getOrder(View view);

        float getWeight(View view);
    }

    public static ViewList buildViewNodeTree(View[] viewArr, final IParamsGetter iParamsGetter) {
        Arrays.sort(viewArr, new Comparator() { // from class: miuix.flexible.mark.MarkHelper$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return MarkHelper.lambda$buildViewNodeTree$0(iParamsGetter, (View) obj, (View) obj2);
            }
        });
        ViewList viewList = new ViewList();
        viewList.setMark(1);
        viewList.setWeight(1.0f);
        viewList.setOrientation(1);
        recursiveBuild(viewArr, 0, viewList, iParamsGetter);
        return viewList;
    }

    static /* synthetic */ int lambda$buildViewNodeTree$0(IParamsGetter iParamsGetter, View view, View view2) {
        return iParamsGetter.getOrder(view) - iParamsGetter.getOrder(view2);
    }

    private static int recursiveBuild(View[] viewArr, int i, ViewList viewList, IParamsGetter iParamsGetter) {
        int mark = viewList.getMark();
        while (i < viewArr.length) {
            int mark2 = iParamsGetter.getMark(viewArr[i]);
            int i2 = mark2 % 2 == 0 ? 0 : 1;
            float weight = iParamsGetter.getWeight(viewArr[i]);
            float groupWeight = iParamsGetter.getGroupWeight(viewArr[i]);
            View view = viewArr[i];
            if (mark2 != mark) {
                if (mark2 <= mark) {
                    if (mark2 > 0) {
                        break;
                    }
                } else {
                    ViewList viewList2 = new ViewList();
                    viewList2.setMark(mark2);
                    viewList2.setOrientation(i2);
                    viewList2.setWeight(groupWeight);
                    viewList.getList().add(viewList2);
                    i = recursiveBuild(viewArr, i, viewList2, iParamsGetter);
                }
            } else {
                ViewNode viewNode = new ViewNode();
                viewNode.setMark(mark2);
                viewNode.setView(view);
                viewNode.setWeight(weight);
                viewList.getList().add(viewNode);
            }
            i++;
        }
        return i;
    }
}
