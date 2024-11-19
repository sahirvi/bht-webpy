from django.shortcuts import redirect, render
from .forms import GameForm
from .models import Games


def game_list(request):
    all_the_games_in_my_function_based_view = Games.objects.all()
    context = {'all_the_games': all_the_games_in_my_function_based_view}
    return render(request, 'game-list.html', context)


def game_detail(request, **kwargs):
    games_id = kwargs['pk']
    that_one_game_in_my_function_based_view = Games.objects.get(id=games_id)
    context = {'that_one_game': that_one_game_in_my_function_based_view}
    return render(request, 'game-detail.html', context)


def game_create(request):
    if request.method == 'POST':
        form_in_my_function_based_view = GameForm(request.POST)
        form_in_my_function_based_view.instance.user = request.user
        if form_in_my_function_based_view.is_valid():
            form_in_my_function_based_view.save()
        else:
            pass

        return redirect('game-list')

    else:
        form_in_my_function_based_view = GameForm()
        context = {'form': form_in_my_function_based_view}
        return render(request, 'game-create.html', context)


def game_delete(request, **kwargs):
    if request.method == 'POST':
        form_in_my_function_based_view = GameForm(request.POST)
        form_in_my_function_based_view.instance.user = request.user
        if form_in_my_function_based_view.is_valid():
            form_in_my_function_based_view.save()
        else:
            pass

        return redirect('game-list')

    else:
        games_id = kwargs['pk']
        that_one_game_in_my_function_based_view = Games.objects.get(id=games_id)
        Games.objects.filter(id=games_id).delete()
        context = {'that_one_game': that_one_game_in_my_function_based_view}
        return render(request, 'game-delete.html', context)
