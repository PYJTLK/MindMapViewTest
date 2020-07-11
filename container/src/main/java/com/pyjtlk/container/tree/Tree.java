package com.pyjtlk.container.tree;

import com.pyjtlk.container.tree.xmlhandler.AbsTreeXmlHandler;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 树
 * @param <D> 结点数据类型
 */
public class Tree<D> {
    protected ArrayList<Tree<D>> mChildren;
    protected D mRootData;

    public Tree(){
        mChildren = new ArrayList<>();
    }

    public Tree(D data){
        this();
        mRootData = data;
    }

    /**
     * 插入子树
     * @param childTree 子树
     */
    public void insertChild(Tree<D> childTree){
        mChildren.add(childTree);
    }

    /**
     * 在指定下标插入子树
     * @param childTree 子树
     * @param index 插入的下标
     */
    public void insertChild(Tree<D> childTree,int index){
        mChildren.add(index,childTree);
    }

    /**
     * 插入多个子树
     * @param children 多个子树
     */
    public void insertChildren(Collection<Tree<D>> children){
        mChildren.addAll(children);
    }

    /**
     * 在指定下标插入多个子树
     * @param children 多个子树
     * @param index 插入下标
     */
    public void insertChildren(Collection<Tree<D>> children,int index){
        mChildren.addAll(index,children);
    }

    /**
     * 移除子树
     * @param index 移除的子树所在下标
     * @return 子树
     */
    public Tree<D> removeChildTree(int index){
        return mChildren.remove(index);
    }

    /**
     * 移除子树
     * @param targetTree 目标子树
     * @return 目标子树
     */
    public boolean removeChildTree(Tree<D> targetTree){
        return mChildren.remove(targetTree);
    }

    /**
     * 获取所有子树
     * @return 所有子树
     */
    public List<Tree<D>> getChildren(){
        return mChildren;
    }

    /**
     * 获取根结点的数据
     * @return 根结点的数据
     */
    public D getRootData() {
        return mRootData;
    }

    /**
     * 设置根结点的数据
     * @param mRootData 根结点的数据
     */
    public void setRootData(D mRootData) {
        this.mRootData = mRootData;
    }

    /**
     * 深度优先遍历所有结点
     * @param nodeSearcher 结点访问器
     */
    public void dfs(NodeSearcher<D> nodeSearcher){
        if(nodeSearcher == null){
            return;
        }

        dfsInside(null,nodeSearcher);
    }

    protected boolean dfsInside(Tree<D> parent,NodeSearcher<D> nodeSearcher){
        boolean isContinue = nodeSearcher.onNode(parent,this);

        if(!isContinue){
            return false;
        }

        if(mChildren != null){
            for(int i = 0;i < mChildren.size();i++){
                Tree<D> child = mChildren.get(i);
                isContinue = nodeSearcher.onNode(this,child);
                if(!isContinue){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 广度优先遍历所有结点
     * @param nodeSearcher 结点访问器
     */
    public void bfs(NodeSearcher<D> nodeSearcher){
        if(nodeSearcher == null){
            return;
        }

        List<Node<Tree<D>>> nodeQueue = new LinkedList<>();
        bfsInside(this,null,nodeSearcher,nodeQueue);
    }

    protected boolean bfsInside(Tree<D> thisNode,Tree<D> parentNode, NodeSearcher<D> nodeSearcher , List<Node<Tree<D>>> nodeQueue) {
        if(!thisNode.isLeaf()){
            if(!nodeSearcher.onNode(parentNode,thisNode)){
                return false;
            }

            List<Tree<D>> childrenNode = thisNode.getChildren();

            for(int i = 0;i < childrenNode.size();i++) {
                Tree<D> childNode = childrenNode.get(i);
                if(!childNode.isLeaf()){
                    nodeQueue.add(new Node(thisNode,childNode));
                }else{
                    boolean continueSearch = nodeSearcher.onNode(thisNode,childNode);
                    if(!continueSearch){
                        return false;
                    }
                }
            }
        }

        if(nodeQueue.size() <= 0){
            return false;
        }

        Node<Tree<D>> treeNode = nodeQueue.remove(0);
        return bfsInside(treeNode.getThisNode(),treeNode.getParentNode(),nodeSearcher,nodeQueue);
    }

    /**
     * 是否为叶结点
     * @return 是否为叶结点
     */
    public boolean isLeaf(){
        return mChildren == null || mChildren.size() == 0;
    }

    /**
     * 解析xml文件生成一棵树
     * @param inputStream xml文件输入流
     * @param treeXmlHandler xml文件解析器
     * @param <D> 结点数据类型
     * @return 解析好的树
     */
    public static <D> Tree<D> parseFromXml(InputStream inputStream, AbsTreeXmlHandler<D> treeXmlHandler){
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(inputStream, treeXmlHandler);
            return treeXmlHandler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
