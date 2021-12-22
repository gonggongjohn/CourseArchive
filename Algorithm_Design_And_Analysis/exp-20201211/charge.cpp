#include<fstream>
#include<cmath>
#include<cstring>
using namespace std;
int m[1000005];
int main(){
    ifstream fin("customer.txt");
    ofstream fout("output.txt");
    int a, rec[8];
    double nom[8] = {100, 50, 20, 10, 5, 1, 0.5, 0.1};
    double res;
    fin>>a;
    for(int i = 0; i < a; i++){
        fin>>m[i];
        res = floor(1.05 * m[i] * 10) / 10 - m[i];
        memset(rec, 0, sizeof(rec));
        for(int j = 0; j < 8; j++){
            if(res / nom[j] >= 1){
                rec[j] = floor(res / nom[j]);
                res -= nom[j] * rec[j];
            }
            if(res < 0.1) break;
        }
        for(int w = 0; w < 8; w++) fout<<rec[w]<<" ";
        fout<<endl;
    }
    fin.close();
    fout.close();
    return 0;
}