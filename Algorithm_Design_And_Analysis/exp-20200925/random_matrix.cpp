#include <iostream>
#include <fstream>
#include <cstdlib>
#include <ctime>
using namespace std;
int main() {
    int n, s, t;
    srand(time(0));
    ofstream fout1("data1.txt");
    ofstream fout2("data2.txt");
    cin>>n>>s>>t;
    for(int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
            fout1 << s + rand() % (t - s)<<" ";
            fout2 << s + rand() % (t - s)<<" ";
        }
        fout1<<endl;
        fout2<<endl;
    }
    fout1.close();
    fout2.close();
    return 0;
}
