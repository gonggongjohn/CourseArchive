#include <iostream>
#include <fstream>
using namespace std;
int main(){
    int a[1000000], n, temp;
    bool flag = true;
    clock_t startTime, endTime;
    startTime = clock();
    ifstream fin("data.txt");
    n = 0;
    while(!fin.eof()){
        fin>>a[n];
        n++;
    }
    n--;
    while(flag){
        flag = false;
        for(int i = 0; i < n - 1; i++){
            if(a[i] > a[i + 1]){
                flag = true;
                temp = a[i];
                a[i] = a[i + 1];
                a[i + 1] = temp;
            }
        }
    }
    for(int w = 0; w < n; w++) cout<<a[w]<<" ";
    endTime = clock();
    cout<<endl;
    cout<<"Run Time: "<< (double) (endTime - startTime) / CLOCKS_PER_SEC<<"s"<<endl;
    fin.close();
    return 0;
}