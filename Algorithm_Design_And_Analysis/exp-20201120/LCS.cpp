#include <iostream>
#include <fstream>
#include <cstdlib>
using namespace std;
int d[5005][5005];
int main(){
    ifstream fin1("data1.txt");
    ifstream fin2("data2.txt");
    clock_t start, stop;
    int a[5005], b[5005], r[5005], n1 = 0, n2 = 0, p, q, cnt;
    cout<<"原始序列1: ";
    while (!fin1.eof()){
        fin1>>a[n1 + 1];
        n1++;
    }
    for(int i = 1; i <= n1 - 1; i++) cout<<a[i]<<" ";
    cout<<endl;
    cout<<"原始序列2: ";
    while(!fin2.eof()){
        fin2>>b[n2 + 1];
        n2++;
    }
    for(int i = 1; i <= n2 - 1; i++) cout<<b[i]<<" ";
    cout<<endl;
    n1--;
    n2--;
    start = clock();
    for(int i = 0; i <= n1; i++)
        for(int j = 0; j <= n2; j++){
            if(i == 0 || j == 0) d[i][j] = 0;
            else{
                if(a[i] == b[j]) d[i][j] = d[i - 1][j - 1] + 1;
                else d[i][j] = max(d[i][j - 1], d[i - 1][j]);
            }
        }
    p = n1;
    q = n2;
    cnt = d[n1][n2] - 1;
    while (p > 0 && q > 0){
        if(d[p][q] == (d[p - 1][q - 1] + 1) && a[p] == b[q]){
            r[cnt] = a[p];
            cnt--;
            p = p - 1;
            q = q - 1;
            continue;
        }
        if(d[p][q-1] == d[p - 1][q]){
            q = q - 1;
        }
        else if(d[p][q] == d[p][q - 1]){
            q = q - 1;
        }
        else{
            p = p - 1;
        }
    }
    stop = clock();
    cout<<"最长公共子序列: ";
    for(int i = 0; i < d[n1][n2]; i++) cout<<r[i]<<" ";
    cout<<endl;
    cout<<"Total time: "<<(double)(stop - start) / CLOCKS_PER_SEC<<endl;
    fin1.close();
    fin2.close();
    return 0;
}