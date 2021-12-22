#include<iostream>
#include<fstream>
#include<cstdlib>
#include<ctime>
using namespace std;
int a[1000005];
void swap(int *m, int *n){
    int tmp;
    tmp = *m;
    *m = *n;
    *n = tmp;
}
int partition(int p, int r){
    int x = a[r];
    int i = p-1;
    for(int j = p;j < r; j++){
        if(a[j] <= x){
            i = i + 1;
            swap(&a[i], &a[j]);
        }
    }
    swap(&a[i+1], &a[r]);
    return i + 1;
}
int random_partition(int p, int r){
    int t = p + rand() % (r - p + 1);
    swap(&a[t], &a[r]);
    return partition(p, r);
}
int random_select(int p, int r, int i){
    if(p == r) return a[p];
    int pivot = random_partition(p, r);
    int k = pivot - p + 1;
    if(i == k) return a[pivot];
    else if(i < k) return random_select(p, pivot - 1, i);
    else return random_select(pivot + 1, r, i - k);
}
int main(){
    srand(time(0));
    clock_t start, stop;
    ifstream fin("data.txt");
    int n, i;
    n = 0;
    while(!fin.eof()){
        fin>>a[n];
        n++;
    }
    n--;
    cin>>i;
    start = clock();
    cout<<random_select(0, n - 1, i)<<endl;
    stop = clock();
    cout<<"Total Time: "<<(double) (stop - start) / CLOCKS_PER_SEC<<endl;
    fin.close();
    return 0;
}