%实验三
%传输时延要求的影响
%topo：newyork

[ha, pos] = tight_subplot(1,2,[.01 .15],[.2 .05],[.06 .02]);

%[ha, pos] = tight_subplot(行, 列, [上下间距,左右间距],[下边距,上边距 ], [左边距,右边距 ])

aPM = 80.5;
aVM = 165.9;

x = [0 1 2 3 4];


y1_0 = [5 5 5 0];
y1_1 = [3 3 4 8];
y1_2 = [1 3 4 8];
y1_3 = [1 2 8 8];



y1 = [y1_3;y1_2;y1_1;y1_0];

axes(ha(1));
b = bar(y1);
grid on;
set(gca,'xticklabel',{'3','2','1','0'});
legend('p=100%','p=50%','p=30%','p=15%');legend boxoff  
xlabel('允许延迟跳数');
ylabel('激活成本')



y2_0 = (y1_0 * aPM .* y1_0/20 + [47 47 50 0 ] * aVM) /1000;
y2_1 = (y1_1 * aPM .* y1_1/20 + [44 45 46 48] * aVM) /1000;
y2_2 = (y1_2 * aPM .* y1_2/20 + [43 43 45 46] * aVM) /1000;
y2_3 = (y1_3 * aPM .* y1_3/20 + [43 44 45 48] * aVM) /1000;

y2 = [y2_3;y2_2;y2_1;y2_0];

axes(ha(2));
b = bar(y2);
grid on;
set(gca,'xticklabel',{'3','2','1','0'});
legend('p=100%','p=50%','p=30%','p=15%');legend boxoff  
xlabel('允许延迟跳数');
ylabel('能耗成本(KW)')