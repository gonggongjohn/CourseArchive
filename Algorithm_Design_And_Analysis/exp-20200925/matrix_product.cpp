#include <iostream>
#include <fstream>
#include <cmath>
#include <ctime>
using namespace std;
int m1[2048][2048], m2[2048][2048], tm1[2048 * 2048], tm2[2048 * 2048];
int main(){
    int cnt, n, tsum;
    clock_t start, stop;
    ifstream fin1("data1.txt");
    ifstream fin2("data2.txt");
    cnt = 0;
    while (!fin1.eof()){
        fin1>>tm1[cnt];
        fin2>>tm2[cnt];
        cnt++;
    }
    cnt--;
    n = sqrt(cnt);
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            m1[i][j] = tm1[i * n + j];
            m2[i][j] = tm2[i * n + j];
        }
    }
    start = clock();
    for(int i = 0; i < n; i++){
        for(int j = 0; j < n; j++){
            tsum = 0;
            for(int k = 0; k < n; k++){
                tsum += m1[i][k] * m2[k][j];
            }
            //cout<<tsum<<" ";
        }
        //cout<<endl;
    }
    stop = clock();
    cout<<"Time Cost: "<<(double)(stop - start) / CLOCKS_PER_SEC<<"s"<<endl;
    fin1.close();
    fin2.close();
    return 0;
}