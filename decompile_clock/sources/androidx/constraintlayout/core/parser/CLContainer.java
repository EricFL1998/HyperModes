package androidx.constraintlayout.core.parser;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class CLContainer extends CLElement {
    ArrayList<CLElement> mElements;

    public CLContainer(char[] cArr) {
        super(cArr);
        this.mElements = new ArrayList<>();
    }

    public static CLElement allocate(char[] cArr) {
        return new CLContainer(cArr);
    }

    public void add(CLElement cLElement) {
        this.mElements.add(cLElement);
        if (CLParser.DEBUG) {
            System.out.println("added element " + cLElement + " to " + this);
        }
    }

    @Override // androidx.constraintlayout.core.parser.CLElement
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (CLElement cLElement : this.mElements) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(cLElement);
        }
        return super.toString() + " = <" + ((Object) sb) + " >";
    }

    public int size() {
        return this.mElements.size();
    }

    public ArrayList<String> names() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (CLElement cLElement : this.mElements) {
            if (cLElement instanceof CLKey) {
                arrayList.add(((CLKey) cLElement).content());
            }
        }
        return arrayList;
    }

    public boolean has(String str) {
        for (CLElement cLElement : this.mElements) {
            if ((cLElement instanceof CLKey) && ((CLKey) cLElement).content().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public void put(String str, CLElement cLElement) {
        Iterator<CLElement> it = this.mElements.iterator();
        while (it.hasNext()) {
            CLKey cLKey = (CLKey) it.next();
            if (cLKey.content().equals(str)) {
                cLKey.set(cLElement);
                return;
            }
        }
        this.mElements.add((CLKey) CLKey.allocate(str, cLElement));
    }

    public void putNumber(String str, float f) {
        put(str, new CLNumber(f));
    }

    public void remove(String str) {
        ArrayList arrayList = new ArrayList();
        for (CLElement cLElement : this.mElements) {
            if (((CLKey) cLElement).content().equals(str)) {
                arrayList.add(cLElement);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            this.mElements.remove((CLElement) it.next());
        }
    }

    public CLElement get(String str) throws CLParsingException {
        Iterator<CLElement> it = this.mElements.iterator();
        while (it.hasNext()) {
            CLKey cLKey = (CLKey) it.next();
            if (cLKey.content().equals(str)) {
                return cLKey.getValue();
            }
        }
        throw new CLParsingException("no element for key <" + str + ">", this);
    }

    public int getInt(String str) throws CLParsingException {
        CLElement cLElement = get(str);
        if (cLElement != null) {
            return cLElement.getInt();
        }
        throw new CLParsingException("no int found for key <" + str + ">, found [" + cLElement.getStrClass() + "] : " + cLElement, this);
    }

    public float getFloat(String str) throws CLParsingException {
        CLElement cLElement = get(str);
        if (cLElement != null) {
            return cLElement.getFloat();
        }
        throw new CLParsingException("no float found for key <" + str + ">, found [" + cLElement.getStrClass() + "] : " + cLElement, this);
    }

    public CLArray getArray(String str) throws CLParsingException {
        CLElement cLElement = get(str);
        if (cLElement instanceof CLArray) {
            return (CLArray) cLElement;
        }
        throw new CLParsingException("no array found for key <" + str + ">, found [" + cLElement.getStrClass() + "] : " + cLElement, this);
    }

    public CLObject getObject(String str) throws CLParsingException {
        CLElement cLElement = get(str);
        if (cLElement instanceof CLObject) {
            return (CLObject) cLElement;
        }
        throw new CLParsingException("no object found for key <" + str + ">, found [" + cLElement.getStrClass() + "] : " + cLElement, this);
    }

    public String getString(String str) throws CLParsingException {
        CLElement cLElement = get(str);
        if (cLElement instanceof CLString) {
            return cLElement.content();
        }
        throw new CLParsingException("no string found for key <" + str + ">, found [" + (cLElement != null ? cLElement.getStrClass() : null) + "] : " + cLElement, this);
    }

    public boolean getBoolean(String str) throws CLParsingException {
        CLElement cLElement = get(str);
        if (cLElement instanceof CLToken) {
            return ((CLToken) cLElement).getBoolean();
        }
        throw new CLParsingException("no boolean found for key <" + str + ">, found [" + cLElement.getStrClass() + "] : " + cLElement, this);
    }

    public CLElement getOrNull(String str) {
        Iterator<CLElement> it = this.mElements.iterator();
        while (it.hasNext()) {
            CLKey cLKey = (CLKey) it.next();
            if (cLKey.content().equals(str)) {
                return cLKey.getValue();
            }
        }
        return null;
    }

    public CLObject getObjectOrNull(String str) {
        CLElement orNull = getOrNull(str);
        if (orNull instanceof CLObject) {
            return (CLObject) orNull;
        }
        return null;
    }

    public CLArray getArrayOrNull(String str) {
        CLElement orNull = getOrNull(str);
        if (orNull instanceof CLArray) {
            return (CLArray) orNull;
        }
        return null;
    }

    public String getStringOrNull(String str) {
        CLElement orNull = getOrNull(str);
        if (orNull instanceof CLString) {
            return orNull.content();
        }
        return null;
    }

    public float getFloatOrNaN(String str) {
        CLElement orNull = getOrNull(str);
        if (orNull instanceof CLNumber) {
            return orNull.getFloat();
        }
        return Float.NaN;
    }

    public CLElement get(int i) throws CLParsingException {
        if (i >= 0 && i < this.mElements.size()) {
            return this.mElements.get(i);
        }
        throw new CLParsingException("no element at index " + i, this);
    }

    public int getInt(int i) throws CLParsingException {
        CLElement cLElement = get(i);
        if (cLElement != null) {
            return cLElement.getInt();
        }
        throw new CLParsingException("no int at index " + i, this);
    }

    public float getFloat(int i) throws CLParsingException {
        CLElement cLElement = get(i);
        if (cLElement != null) {
            return cLElement.getFloat();
        }
        throw new CLParsingException("no float at index " + i, this);
    }

    public CLArray getArray(int i) throws CLParsingException {
        CLElement cLElement = get(i);
        if (cLElement instanceof CLArray) {
            return (CLArray) cLElement;
        }
        throw new CLParsingException("no array at index " + i, this);
    }

    public CLObject getObject(int i) throws CLParsingException {
        CLElement cLElement = get(i);
        if (cLElement instanceof CLObject) {
            return (CLObject) cLElement;
        }
        throw new CLParsingException("no object at index " + i, this);
    }

    public String getString(int i) throws CLParsingException {
        CLElement cLElement = get(i);
        if (cLElement instanceof CLString) {
            return cLElement.content();
        }
        throw new CLParsingException("no string at index " + i, this);
    }

    public boolean getBoolean(int i) throws CLParsingException {
        CLElement cLElement = get(i);
        if (cLElement instanceof CLToken) {
            return ((CLToken) cLElement).getBoolean();
        }
        throw new CLParsingException("no boolean at index " + i, this);
    }

    public CLElement getOrNull(int i) {
        if (i < 0 || i >= this.mElements.size()) {
            return null;
        }
        return this.mElements.get(i);
    }

    public String getStringOrNull(int i) {
        CLElement orNull = getOrNull(i);
        if (orNull instanceof CLString) {
            return orNull.content();
        }
        return null;
    }
}
