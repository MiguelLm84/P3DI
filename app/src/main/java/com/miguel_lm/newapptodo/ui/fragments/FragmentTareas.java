package com.miguel_lm.newapptodo.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miguel_lm.newapptodo.R;
import com.miguel_lm.newapptodo.core.Tarea;
import com.miguel_lm.newapptodo.core.TareaLab;
import com.miguel_lm.newapptodo.ui.ListenerTareas;
import com.miguel_lm.newapptodo.ui.adaptador.AdapterTareas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FragmentTareas extends Fragment implements ListenerTareas {

    public static FragmentTareas FragmentTareasInstance;

    AdapterTareas adapterTareas;
    private LinearLayout toolBar;
    List<Tarea> listaTareasSeleccionadas;
    ArrayList<Tarea> listaTareasFinalizadas;
    TareaLab tareaLab;
    List<Tarea> listaTareas;
    private ImageView imageButtonModificarTarea;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentTareas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentTareas newInstance(String param1, String param2) {
        FragmentTareas fragment = new FragmentTareas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tareas, container, false);

        FragmentTareasInstance = this;

        toolBar = root.findViewById(R.id.toolbar);
        toolBar.setVisibility(View.GONE);

        listaTareasSeleccionadas = new ArrayList<>();

        ImageView imageButtonEliminarTarea = root.findViewById(R.id.btn_delete);
        ImageView imageButtonSalirToolbar = root.findViewById(R.id.btn_salir);
        imageButtonModificarTarea = root.findViewById(R.id.btn_modificar);

        tareaLab = TareaLab.get(getContext());
        listaTareas = tareaLab.getTareas();

        RecyclerView recyclerViewTareas = root.findViewById(R.id.recyclerViewTareas);
        recyclerViewTareas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterTareas = new AdapterTareas(getContext(), listaTareas, this);
        recyclerViewTareas.setAdapter(adapterTareas);

        imageButtonSalirToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToolbarSalir();
            }
        });

        imageButtonModificarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onClickToolbarModificar(tarea);
                crearTarea();
            }
        });

        imageButtonEliminarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickToolbarEliminar();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Date fechaActual = new Date();
        listaTareasFinalizadas = new ArrayList<>();

        for (Tarea tarea : listaTareas) {

            if (tarea.getFechaLimite().compareTo(fechaActual) < 0) {
                listaTareasFinalizadas.add(tarea);
            }
        }

        if (!listaTareasFinalizadas.isEmpty()) {
            mostrarTareasCaducadas(listaTareasFinalizadas);
        }
    }

    public void refrescarListado() {

        adapterTareas.actualizarListado(TareaLab.get(getContext()).getTareas());
    }

    ////////////////////////////////////////////////////////////////
    // LISTENER VIEW HOLDER TAREA
    ////////////////////////////////////////////////////////////////

    @Override
    public void seleccionarTarea(Tarea tarea) {

        if (tarea.isTareaSeleccionada()) {
            listaTareasSeleccionadas.add(tarea);
        } else {
            listaTareasSeleccionadas.remove(tarea);
        }

        toolBar.setVisibility(listaTareasSeleccionadas.isEmpty() ? View.GONE : View.VISIBLE);

        imageButtonModificarTarea.setVisibility(listaTareasSeleccionadas.size() == 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {

        refrescarListado();
    }

    @Override
    public void seleccionarTareasFavAdd(Tarea tarea) {

    }

    @Override
    public void seleccionarTareasFavRemove(Tarea tarea) {

    }

    @Override
    public void completarTarea(Tarea tarea, boolean completada) {

    }

    ////////////////////////////////////////////////////////////////
    // TOOLBAR
    ////////////////////////////////////////////////////////////////

    public void onClickToolbarEliminar() {

        AlertDialog.Builder builderEliminar = new AlertDialog.Builder(getContext());
        builderEliminar.setIcon(R.drawable.eliminar);
        builderEliminar.setTitle("Eliminar elementos");

        String[] arrayTareas = new String[listaTareasSeleccionadas.size()];
        final boolean[] tareasSeleccionadasParaBorrar = new boolean[listaTareasSeleccionadas.size()];
        for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
            arrayTareas[i] = "\n· TAREA: " + listaTareasSeleccionadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasSeleccionadas.get(i).getFechaTextoCorta();
        }
        builderEliminar.setMultiChoiceItems(arrayTareas, tareasSeleccionadasParaBorrar, (dialog, indiceSeleccionado, isChecked) -> {
            tareasSeleccionadasParaBorrar[indiceSeleccionado] = isChecked;
        });

        builderEliminar.setPositiveButton("Borrar", (dialog, which) -> {

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(getContext());
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setTitle("¿Eliminar los elementos?");
            String textoNombresTareas = "";


            // Generar array con los nombres de las tareas a borrar
            ArrayList<String> listaTareasAeliminar = new ArrayList<>();
            for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
                if (tareasSeleccionadasParaBorrar[i]) {
                    String tareasParaEliminar = "\n· TAREA: " + listaTareasSeleccionadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasSeleccionadas.get(i).getFechaTextoCorta() + "\n";
                    listaTareasAeliminar.add(tareasParaEliminar);
                }
            }

            // Y convertir el array de nombres en un solo string
            for (int i = 0; i < listaTareasAeliminar.size(); i++) {
                textoNombresTareas += listaTareasAeliminar.get(i) + ", ";
            }

            // Configurar el dialog
            builderEliminar_Confirmar.setMessage(textoNombresTareas);
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which1) -> {

                // Recorrer las tareas a borrar y eliminarlas de la BD
                for (int i = 0; i < listaTareasSeleccionadas.size(); i++) {
                    if (tareasSeleccionadasParaBorrar[i]) {
                        tareaLab.get(getContext()).deleteTarea(listaTareasSeleccionadas.get(i));
                    }
                }
                listaTareasSeleccionadas.clear();

                Toast.makeText(getContext(), "Tareas eliminadas correctamente", Toast.LENGTH_SHORT).show();

                refrescarListado();
            });
            builderEliminar_Confirmar.create().show();
            dialog.dismiss();
        });

        builderEliminar.setNegativeButton("Cancelar", null);
        builderEliminar.create().show();
    }

    private void crearTarea() {
        onClickToolbarModificar(null);
    }

    public void onClickToolbarModificar(Tarea tareaAmodificar) {
        
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        final View dialogLayout = LayoutInflater.from(getContext()).inflate(R.layout.dialog_crear_tarea, null);
        build.setView(dialogLayout);
        final AlertDialog dialog = build.create();

        final EditText edTxtTarea = dialogLayout.findViewById(R.id.edTxt_tarea);
        final TextView tvFecha = dialogLayout.findViewById(R.id.tv_fecha);
        final Button buttonAceptar = dialogLayout.findViewById(R.id.btn_aceptar);
        final Button buttonCancelar = dialogLayout.findViewById(R.id.btn_cancelar);

        final Calendar calendar = Calendar.getInstance();

        if (tareaAmodificar != null) {
            edTxtTarea.setText(tareaAmodificar.getTitulo());
            tvFecha.setText(tareaAmodificar.getFechaTexto());
        }

        tvFecha.setOnClickListener(v -> {

            if (tareaAmodificar != null) {
                calendar.setTime(tareaAmodificar.getFechaLimite());
            }

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            final DatePickerDialog dpd = new DatePickerDialog(getContext(), (datePicker, year1, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year1);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd MMMM 'de' yyyy", new Locale("es","ES"));
                tvFecha.setText(formatoFecha.format(calendar.getTime()));
            }, year, month, day);
            dpd.show();
        });

        buttonAceptar.setOnClickListener(v -> {

            String titulo = edTxtTarea.getText().toString();

            if(edTxtTarea.getText().toString().length()<=0){
                Toast.makeText(getContext(), "Debe introducir un título", Toast.LENGTH_SHORT).show();
                return;
            }

            tareaAmodificar.modificar(titulo, calendar.getTime());
            tareaLab.get(getContext()).updateTarea(tareaAmodificar);
            Toast.makeText(getContext()," Tarea modificada correctamente en la BD.",Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), "Evento modificado.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        buttonCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void onClickToolbarSalir() {
        for (Tarea tarea : listaTareasSeleccionadas)
            tarea.setTareaSeleccionada(false);

        listaTareasSeleccionadas.clear();
        toolBar.setVisibility(View.GONE);
        adapterTareas.notifyDataSetChanged();
        Toast.makeText(getContext(), "Salir sin seleccionar", Toast.LENGTH_SHORT).show();
    }

    private void mostrarTareasCaducadas(final List<Tarea> listaTareasFinalizadas) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.eliminar);
        builder.setTitle("Tareas Finalizadas");

        String listaTareasParaBorrar = null;
        String[] arrayTareas = new String[listaTareasFinalizadas.size()];
        final boolean[] tareasSeleccionadas = new boolean[listaTareasFinalizadas.size()];
        for (int i = 0; i < listaTareasFinalizadas.size(); i++) {
            arrayTareas[i] = "\n· TAREA: " + listaTareasFinalizadas.get(i).getTitulo() + "\n· FECHA:  " + listaTareasFinalizadas.get(i).getFechaTextoCorta();
            listaTareasParaBorrar =  arrayTareas[i];
        }
        builder.setMultiChoiceItems(arrayTareas, tareasSeleccionadas, (dialog, i, isChecked) -> tareasSeleccionadas[i] = isChecked);

        final String finalListaTareasParaBorrar = listaTareasParaBorrar;
        builder.setPositiveButton("Borrar", (dialog, which) -> {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setIcon(R.drawable.eliminar);
            builder1.setTitle("¿Eliminar el elemento?");
            builder1.setMessage(finalListaTareasParaBorrar);

            AlertDialog.Builder builderEliminar_Confirmar = new AlertDialog.Builder(getContext());
            builderEliminar_Confirmar.setIcon(R.drawable.exclamation);
            builderEliminar_Confirmar.setMessage("¿Eliminar los elementos?");
            builderEliminar_Confirmar.setNegativeButton("Cancelar", null);
            builderEliminar_Confirmar.setPositiveButton("Borrar", (dialogInterface, which1) -> {

                for (int i = listaTareasFinalizadas.size() - 1; i >= 0; i--) {
                    if (tareasSeleccionadas[i]) {
                        listaTareas.remove(listaTareasFinalizadas.get(i));
                        tareaLab.get(getContext()).deleteTarea(listaTareasFinalizadas.get(i));
                        //todo: método eliminar a BD.
                    }
                }
                Toast.makeText(getContext(), "Tareas eliminadas correctamente", Toast.LENGTH_SHORT).show();
                this.adapterTareas.notifyDataSetChanged();
            });
            builderEliminar_Confirmar.create().show();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }
}