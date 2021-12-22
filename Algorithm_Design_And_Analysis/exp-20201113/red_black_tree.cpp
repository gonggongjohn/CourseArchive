#include <iostream>
#include <fstream>
#include <cstdlib>
using namespace std;
struct node{
    int data;
    int color; //0 is black, 1 is red
    struct node *parent;
    struct node *lchild;
    struct node *rchild;
};
void lrotate(struct node *n){
    struct node *nr = n->rchild;
    n->rchild = nr->lchild;
    if (nr->lchild) nr->lchild->parent = n;
    nr->parent = n->parent;
    if(n->parent) {
        if (n == n->parent->lchild) n->parent->lchild = nr;
        else n->parent->rchild = nr;
    }
    nr->lchild = n;
    n->parent = nr;
}
void rrotate(struct node *n){
    struct node *nl = n->lchild;
    n->lchild = nl->rchild;
    if (nl->rchild) nl->rchild->parent = n;
    nl->parent = n->parent;
    if(n->parent) {
        if (n == n->parent->lchild) n->parent->lchild = nl;
        else n->parent->rchild = nl;
    }
    nl->rchild = n;
    n->parent = nl;
}
struct node *fixup(struct node *n){
    struct node *t;
    while(n->parent && n->parent->color == 1){
        if(n->parent == n->parent->parent->lchild){
            t = n->parent->parent->rchild;
            if (t && t->color == 1){
                n->parent->color = 0;
                t->color = 0;
                n->parent->parent->color = 1;
                n = n->parent->parent;
                if(!n->parent) n->color = 0;
            }
            else {
                if (n == n->parent->rchild) {
                    n = n->parent;
                    lrotate(n);
                }
                n->parent->color = 0;
                n->parent->parent->color = 1;
                rrotate(n->parent->parent);
            }
        }
        else{
            t = n->parent->parent->lchild;
            if (t && t->color == 1){
                n->parent->color = 0;
                t->color = 0;
                n->parent->parent->color = 1;
                n = n->parent->parent;
                if(!n->parent) n->color = 0;
            }
            else {
                if (n == n->parent->lchild) {
                    n = n->parent;
                    rrotate(n);
                }
                n->parent->color = 0;
                n->parent->parent->color = 1;
                lrotate(n->parent->parent);
            }
        }
    }
    t = n;
    while (t->parent){
        t = t->parent;
    }
    t->color = 0;
    return t;
}
struct node *insert(struct node *root, struct node *n){
    struct node *x, *y, *nroot;
    x = root;
    while(x){
        y = x;
        if(n->data < x->data){
            x = x->lchild;
        }
        else{
            x = x->rchild;
        }
    }
    n->parent = y;
    if(n->data < y->data){
        y->lchild = n;
    }
    else{
        y->rchild = n;
    }
    n->color = 1;
    nroot = fixup(n);
    return nroot;
}
void traverse(struct node *n){
    if(!n) return;
    traverse(n->lchild);
    cout<<n->data<<" "<<n->color<<endl;
    traverse(n->rchild);
}
int main(){
    ifstream fin("data.txt");
    clock_t start, stop;
    int a[1000005], n = 0;
    while (!fin.eof()){
        fin>>a[n];
        n++;
    }
    n--;
    start = clock();
    node *r = new node{a[0], 0, 0x0, 0x0};
    for(int i = 1; i < n; i++){
        node *n = new node{a[i], 1, 0x0, 0x0};
        r = insert(r, n);
    }
    stop = clock();
    //traverse(r);
    cout<<"Total Time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<"s"<<endl;
    fin.close();
    return 0;
}