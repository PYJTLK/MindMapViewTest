package com.pyjtlk.container.tree.xmlhandler;

import com.pyjtlk.container.stack.Stack;
import com.pyjtlk.container.tree.Tree;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public abstract class AbsTreeXmlHandler<D> extends DefaultHandler {
    public static final String XML_ELEMENT_TREE = "tree";
    public static final String XML_ELEMENT_NODE = "node";
    public static final String XML_ELEMENT_DATA = "data";

    private Tree<D> mTree;
    private Stack<Tree<D>> mStack;

    @Override
    public void startDocument() throws SAXException {
        mTree = new Tree<>();
        mStack = new Stack<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Tree<D> parent;
        D data = parseData(attributes.getValue(XML_ELEMENT_DATA));

        if(mStack.empty()){
            parent = null;
        }else{
            parent = mStack.pop();
        }

        switch(qName){
            case XML_ELEMENT_NODE:
                Tree<D> node = new Tree<>();
                node.setRootData(data);
                parent.insertChild(node);
                mStack.push(parent);
                break;

            case XML_ELEMENT_TREE:
                if(parent == null){
                    mTree.setRootData(data);
                    mStack.push(mTree);
                    return;
                }

                Tree<D> subTree = new Tree<>();
                subTree.setRootData(data);
                parent.insertChild(subTree);
                mStack.push(parent);
                mStack.push(subTree);
                break;

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch(qName){
            case XML_ELEMENT_TREE:
                if(!mStack.empty()){
                    mStack.pop();
                    return;
                }
                break;
        }
    }

    public Tree<D> getResult(){
        return mTree;
    }

    public abstract D parseData(String sourceData);
}
