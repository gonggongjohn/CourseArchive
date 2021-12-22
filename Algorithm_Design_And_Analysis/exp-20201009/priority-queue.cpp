#include <iostream>
#include <fstream>
#include <cstdlib>
using namespace std;
int a[1000005], cur;
void siftUp(int index){
    int parent, tmp;
    parent = (index - 1) / 2;
    while(index != 0 && a[index] > a[parent]){
        tmp = a[parent];
        a[parent] = a[index];
        a[index] = tmp;
        index = parent;
        parent = (index - 1) / 2;
    }
}
void siftDown(int index){
    int lchild, rchild, tmp, tcur;
    lchild = index * 2 + 1;
    rchild = (index + 1) * 2;
    while(rchild <= cur && (a[index] < a[lchild] || a[index] < a[rchild])){
        tcur = a[lchild] > a[rchild] ? lchild : rchild;
        tmp = a[tcur];
        a[tcur] = a[index];
        a[index] = tmp;
        index = tcur;
        lchild = tcur * 2 + 1;
        rchild = (tcur + 1) * 2;
    }
    if(lchild <= cur && a[index] < a[lchild]){
        tmp = a[lchild];
        a[lchild] = a[index];
        a[index] = tmp;
    }
}
void add(int data){
    cur++;
    a[cur] = data;
    siftUp(cur);
}
int popMax(){
    int r = a[0];
    a[0] = a[cur];
    cur--;
    siftDown(0);
    return r;
}
void heapify(){
    int tcur;
    tcur = (cur - 1) / 2;
    while(tcur >= 0){
        siftDown(tcur);
        tcur--;
    }
}
int main(){
    ifstream fin("data.txt");
    clock_t start, stop;
    cur = 0;
    while(!fin.eof()){
        fin>>a[cur];
        cur++;
    }
    cur--;
    start = clock();
    heapify();
    stop = clock();
    for(int i = 0; i < cur; i++) cout<<a[i]<<" ";
    cout<<endl;
    cout<<"Total time: "<<((double) (stop - start)) / CLOCKS_PER_SEC<<endl;
    fin.close();
    return 0;
}