#include<iostream>
#include<fstream>
using namespace std;
int main(){
    int a[1000000], n, key, j;
    clock_t startTime, endTime;
    startTime = clock();
    ifstream fin("data.txt");
    n = 0;
    while(!fin.eof()){
        fin>>a[n];
        n++;
    }
    n--;
    for(int i = 1; i < n; i++){
        key = a[i];
        j = i - 1;
        while(j >= 0 && a[j] > key){
            a[j + 1] = a[j];
            j--;
        }
        a[j + 1] = key;
    }
    for(int w = 0; w < n; w++){
        cout<<a[w]<<" ";
    }
    endTime = clock();
    cout<<endl;
    cout<<"Run Time: "<<(double) (endTime - startTime) / CLOCKS_PER_SEC<<"s"<<endl;
    fin.close();
    return 0;
}